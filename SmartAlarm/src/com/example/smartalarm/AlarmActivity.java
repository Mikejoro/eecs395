package com.example.smartalarm;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;

// Triggered by SensorService when it's time to wake up
public class AlarmActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sleep_mode);

		if (SensorService.isRunning(this)) {
			SensorService.stop(this);
		}
		//[TODO] start playing alarm
	}

	public void awakeButtonClicked(View view) {
		//[TODO] stop playing alarm
		finish();
	}

}
