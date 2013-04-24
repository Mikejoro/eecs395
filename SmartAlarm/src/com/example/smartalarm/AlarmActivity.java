package com.example.smartalarm;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;

//Triggered by SensorService when it's time to wake up
public class AlarmActivity extends Activity {

	private MediaPlayer mp;
	private Vibrator v;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_sleep_mode);

		if (SensorService.isRunning(this)) {
			SensorService.stop(this);
		}
		hdlr = new AlarmHandler(this);
		triggerAlarm();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sleep_mode, menu);
		return true;
	}

	public void awakeButtonClicked(View view) {
		if(mp != null)
		{
			
			if(mp.isPlaying()){
				try{
					mp.stop();
				}catch(IllegalStateException e){}
			}
			try{
				mp.release();
			}catch(IllegalStateException e){}
			
		}
		if(v != null)
		{
			v.cancel();
		}
		//Intent intent = new Intent(this, MainActivity.class);
		//startActivity(intent);
		finish();
	}

	public void triggerAlarm() {
		hdlr.sendEmptyMessage(0);
	}

	//this prevents ui thread issues
	private static class AlarmHandler extends Handler {
		private AlarmActivity act = null;
		public void handleMessage(android.os.Message msg) {
			act.doAlarm();
		}
		public AlarmHandler(AlarmActivity a) {
			act = a;
		}
	};
	private static AlarmHandler hdlr = null;

	private void doAlarm() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String uri = prefs.getString("alarm_ringtone", "");
				/*
		//debug textviews
		TextView tvTone = (TextView) findViewById(R.id.testToneUri);
		TextView tvVibrate = (TextView) findViewById(R.id.testVibrateOn);
		tvTone.setText(uri);
		tvVibrate.setText(String.valueOf(prefs.getBoolean("alarm_vibrate", false)));
		TextView wake = (TextView) findViewById(R.id.testmediathreading);
		wake.setText("sleep");*/

		mp = MediaPlayer.create(this, Uri.parse(uri));
		if(mp != null)
		{
			mp.setLooping(true);
			mp.setOnCompletionListener(new OnCompletionListener() {
            	@Override
            	public void onCompletion(MediaPlayer mp) {
            		mp.start();
            	}
        	});   
        	mp.start();
		}

		if(prefs.getBoolean("alarm_vibrate", false))
		{
			// Get instance of Vibrator from current Context
			v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

			// Start immediately
			// Vibrate for 200 milliseconds
			// Sleep for 500 milliseconds
			long[] pattern = { 0, 200, 500 };

			// The "0" means to repeat the pattern starting at the beginning
			v.vibrate(pattern, 0);
		}
	}
}
