package com.example.smartalarm;

import java.util.concurrent.LinkedBlockingQueue;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/*
 * Fills a given LinkedBlockingQueue with float[]s corresponding to accelerometer data {x, y, z}.
 */
public class AccelThread extends Thread implements SensorEventListener {

	private boolean running = false;
	private LinkedBlockingQueue<AccelDataPoint> output;
	private SensorManager manager;
	
	public AccelThread(LinkedBlockingQueue<AccelDataPoint> sharedQueue, Context ctx) {
		setDaemon(true);
		output = sharedQueue;
		manager = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
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
			output.offer(new AccelDataPoint(System.currentTimeMillis(), event.values));
		}
	}
}
