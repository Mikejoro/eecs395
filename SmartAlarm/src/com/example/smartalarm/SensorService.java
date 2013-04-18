package com.example.smartalarm;

import java.util.concurrent.LinkedBlockingQueue;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;

public class SensorService extends Service implements SensorEventListener {
	
	private static final String TAG = SensorService.class.getName();
	private static final int SCREEN_OFF_RECEIVER_DELAY = 500;
	private static final long WAIT_THREAD_CLOSE = 1000;

	//private Context mContext;
	private SensorManager mSensorManager;
	private Sensor mSensor;
	//private PowerManager mPowerManager;
	//private WakeLock mWakeLock;
	
	private LinkedBlockingQueue<AccelDataPoint> sharedQueue;
	private ConsumerThread mThread;

	private void registerListener() {
		mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
	}

	private void unregisterListener() {
		mSensorManager.unregisterListener(this);
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (!intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
				return;
			}
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					unregisterListener();
					registerListener();
				}
			}, SCREEN_OFF_RECEIVER_DELAY);
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		//mContext = getApplicationContext();
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		//mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		//mWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
		registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
		sharedQueue = new LinkedBlockingQueue<AccelDataPoint>();
		mThread = new ConsumerThread(sharedQueue);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		startForeground(Process.myPid(), new Notification());
		registerListener();
		//mWakeLock.acquire();
		mThread.start();
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		mThread.interrupt();
		try {
			mThread.join(WAIT_THREAD_CLOSE);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		unregisterReceiver(mReceiver);
		unregisterListener();
		//mWakeLock.release();
		stopForeground(true);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}

	@Override
	public void onSensorChanged(SensorEvent event) {
		switch (event.sensor.getType()) {
			case Sensor.TYPE_ACCELEROMETER: {
				Log.i(TAG, "sensor");
				sharedQueue.offer(new AccelDataPoint(System.currentTimeMillis(), event.values.clone()));
			}
		}
	}

	private class ConsumerThread extends Thread {

		private final String TAG = ConsumerThread.class.getName();

		private LinkedBlockingQueue<AccelDataPoint> output;

		public ConsumerThread(LinkedBlockingQueue<AccelDataPoint> sharedQueue) {
			setDaemon(true);
			output = sharedQueue;
		}

		@Override
		public void run() {
			Log.d(TAG, "thread started");
			while (true) {
				AccelDataPoint data;
				try {
					// LinkedBlockingQueue.take() is a blocking operation
					data = output.take();
				} catch (InterruptedException e) {
					// thread is closing
					break;
				}
				//[TODO] process the data here
			}
			//[TODO] cleanup here
			Log.d(TAG, "thread finished");
		}

	}

}
