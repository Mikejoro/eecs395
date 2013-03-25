package com.example.smartalarm;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class SleepModeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_sleep_mode);
		
		Intent intent = getIntent();
		
		//The start time and end time for the alarm window are passed into the activity on its creation
		int windowStartHours 	= intent.getIntExtra(ConfirmSleepAlarmActivity.START_ALARM_HOURS, 6);
		int windowStartMinutes 	= intent.getIntExtra(ConfirmSleepAlarmActivity.START_ALARM_MINUTES, 30);
		int windowEndHours 		= intent.getIntExtra(ConfirmSleepAlarmActivity.FAILSAFE_ALARM_HOURS, 7);
		int windowEndMinutes 	= intent.getIntExtra(ConfirmSleepAlarmActivity.FAILSAFE_ALARM_HOURS, 30);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sleep_mode, menu);
		return true;
	}

}
