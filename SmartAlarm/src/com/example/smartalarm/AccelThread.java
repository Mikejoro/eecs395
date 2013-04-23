package com.example.smartalarm;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Fills a given LinkedBlockingQueue with float[]s corresponding to accelerometer data {x, y, z}.
 */
public class AccelThread extends Thread {

	private LinkedBlockingQueue<AccelDataPoint> q;
	private ArrayList<Long> output = new ArrayList<Long>();
	private Context ctx;
	private double sensitivity = 0.;
	private float lastX = 0, lastY = 0, lastZ = 0;
	private int fellAsleepIdx = -1; //output[fellAsleepIdx] is first data point when user is asleep
	private long after, before;
	
	private List<Long> asleepData() {
		if (fellAsleepIdx == -1)
			return new ArrayList<Long>();
		return output.subList(fellAsleepIdx, output.size());
	}
	
	/**
	 * New accelerometer monitoring thread
	 * @param ctx App Context
	 * @param sensty Minimum accelerometer value 
	 */
	public AccelThread(LinkedBlockingQueue<AccelDataPoint> q, Context context, double sensty, long wakeAfter, long wakeBefore) {
		setDaemon(true);
		this.q = q;
		sensitivity = sensty * sensty; //square now so we don't have to square root all the time
		ctx = context;
		after = wakeAfter;
		before = wakeBefore;
	}

	@Override
	public void run() {
		while (true) {
			AccelDataPoint data;
			try {
				// LinkedBlockingQueue.take() is a blocking operation
				data = q.take();
			} catch (InterruptedException e) {
				// thread is closing
				break;
			}
			// process the data here
			onSensorChanged(data);
			if (checkWakeup()) {
				//activity.triggerAlarm();
				((SensorService) ctx).triggerAlarm();
				break;
			}
		}
		//[TODO] cleanup here
	}

	private void onSensorChanged(AccelDataPoint p) {
		float x = p.data[0] - lastX, y = p.data[1] - lastY, z = p.data[2] - lastZ;
		lastX = p.data[0]; lastY = p.data[1]; lastZ = p.data[2];
			
		if (x*x + y*y + z*z < sensitivity)
			return;
		
		synchronized (output) {
			//if they haven't moved over an hour they probably fell asleep
			if (fellAsleepIdx == -1 && output.size() != 0 && p.time - output.get(output.size()-1) > 60 * 60 * 1000)
				fellAsleepIdx = output.size();
			
			output.add(p.time);
		}
	}
	
	private boolean checkWakeup() {
		if (fellAsleepIdx == -1)
			return false; //f'kn insomniac...
		
		final long period = 90 * 60 * 1000; //assume sleep cycle length is 90 minutes
		long phase;
		
		synchronized (output) {
			//the phase is relative to the epoch
			phase = Analytics.ClockRecover(asleepData(), period);
		}
		
		long idealTime = ((after - phase)/period + 1) * period + phase;
		long now = System.currentTimeMillis();
		
		return now > before //failsafe
				|| now > idealTime;
	}
}