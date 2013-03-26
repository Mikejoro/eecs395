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
	private LinkedBlockingQueue<float[]> output;
	
	public AccelThread(LinkedBlockingQueue<float[]> sharedQueue, Context ctx) {
		setDaemon(true);
		output = sharedQueue;
		SensorManager manager = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
		manager.registerListener(this, manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	@Override
	public void run() {
		running = true;
	}
	
	public void close() {
		running = false;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (running && event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			output.offer(event.values);
		}
	}
}
