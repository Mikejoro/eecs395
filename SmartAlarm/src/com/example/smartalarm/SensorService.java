package com.example.smartalarm;

import java.util.concurrent.LinkedBlockingQueue;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.Service;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Process;
import android.preference.PreferenceManager;
import android.util.Log;

public class SensorService extends Service implements SensorEventListener {

	private static final String TAG = SensorService.class.getName();
	private static final int SCREEN_OFF_RECEIVER_DELAY = 500;
	private static final long WAIT_THREAD_CLOSE = 1000;

	private long minTime;
	private long maxTime;

	public static void start(Context context, long minTime, long maxTime) {
		Intent intent = new Intent(context, SensorService.class);
		intent.putExtra("minTime", minTime);
		intent.putExtra("maxTime", maxTime);
        context.startService(intent);
	}

	public static void stop(Context context) {
		context.stopService(new Intent(context, SensorService.class));
	}

	public static boolean isRunning(Context context) {
		return isServiceRunning(context, SensorService.class);
	}

	private static boolean isServiceRunning(Context context, Class<?> klass) {
		String name = klass.getName();
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (service.service.getClassName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	private Context mContext;
	private SharedPreferences mPrefs;
	private SensorManager mSensorManager;
	private Sensor mSensor;
	//private PowerManager mPowerManager;
	//private WakeLock mWakeLock;

	private LinkedBlockingQueue<AccelDataPoint> sharedQueue;
	private Thread mThread;

	private void registerListener() {
		mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
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
		//mContext = this.getApplicationContext();
		mContext = this.getBaseContext();
		mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		//mPowerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
		//mWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
		mContext.registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));

		sharedQueue = new LinkedBlockingQueue<AccelDataPoint>();
		if (mPrefs.getBoolean("ck_accel", true)) {
			mThread = new AccelThread(sharedQueue, this, 0.2, minTime, maxTime);
		} else {
			mThread = new DummyThread(sharedQueue);
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		minTime = intent.getLongExtra("minTime", 0);
		maxTime = intent.getLongExtra("maxTime", 0);
		startForeground(Process.myPid(), new Notification());
		registerListener();
		//mWakeLock.acquire();
		mThread.start();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				triggerAlarm();
			}
		//[TODO] calculate delay necessary to trigger at maxTime
		}, 10 * 1000);
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
		mContext.unregisterReceiver(mReceiver);
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

	public void triggerAlarm() {
		Intent intent = new Intent(mContext, AlarmActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getApplication().startActivity(intent);
		stopSelf();
	}

	private class DummyThread extends Thread {

		private final String TAG = DummyThread.class.getName();

		private LinkedBlockingQueue<AccelDataPoint> output;

		public DummyThread(LinkedBlockingQueue<AccelDataPoint> output) {
			setDaemon(true);
			this.output = output;
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
				if (data != null) data = null;
			}
			//[TODO] cleanup here
			Log.d(TAG, "thread finished");
		}

	}

}
