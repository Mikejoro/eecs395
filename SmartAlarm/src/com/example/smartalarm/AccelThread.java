package com.example.smartalarm;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Fills a given LinkedBlockingQueue with float[]s corresponding to accelerometer data {x, y, z}.
 */
public class AccelThread extends Thread implements SensorEventListener {

	private boolean running = false;
	private ArrayList<Long> output = new ArrayList<Long>();
	private SensorManager manager;
	private SleepModeActivity activity;
	private double sensitivity = 0.;
	private float lastX = 0, lastY = 0, lastZ = 0;
	
	/**
	 * New accelerometer monitoring thread
	 * @param ctx App Context
	 * @param sensty Minimum accelerometer value 
	 */
	public AccelThread(SleepModeActivity act, double sensty) {
		setDaemon(true);
		sensitivity = sensty * sensty; //square now so we don't have to square root all the time
		activity = act;
		manager = (SensorManager) act.getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
	}
	
	@Override
	public void run() {
		running = true;
		manager.registerListener(this, manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	public void close() {
		manager.unregisterListener(this);
		running = false;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (running && event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			float x = event.values[0] - lastX, y = event.values[1] - lastY, z = event.values[2] - lastZ;
			lastX = event.values[0]; lastY = event.values[1]; lastZ = event.values[2];
			
			if (x*x + y*y + z*z > sensitivity)
				output.add(System.currentTimeMillis());
		}
	}
}
