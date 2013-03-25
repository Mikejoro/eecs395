package com.example.smartalarm;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.TimePicker;

public class ConfirmSleepAlarmActivity extends Activity {
	
	public static final String START_ALARM_HOURS = "com.example.smartalarm.START_ALARM_HOURS";
	public static final String START_ALARM_MINUTES = "com.example.smartalarm.START_ALARM_MINUTES";
	public static final String FAILSAFE_ALARM_HOURS = "com.example.smartalarm.FAILSAVE_ALARM_HOURS";
	public static final String FAILSAFE_ALARM_MINUTES = "com.example.smartalarm.FAILSAFE_ALARM_MINUTES";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confirm_sleep_alarm);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.confirm_sleep_alarm, menu);
		return true;
	}
	public void onConfirmAlarmClick(View view) {
		
		TimePicker timepicker = (TimePicker) findViewById(R.id.alarmTimePicker);
		int timeHours = timepicker.getCurrentHour();
		int timeMinutes = timepicker.getCurrentMinute();
		TimePicker failsafePicker = (TimePicker) findViewById(R.id.alarmFailsafePicker);
		int failsafeHours = failsafePicker.getCurrentHour();
		int failsafeMinutes = failsafePicker.getCurrentMinute();
		Intent intent = new Intent(this, SleepModeActivity.class);
		intent.putExtra(START_ALARM_HOURS, timeHours);
		intent.putExtra(START_ALARM_MINUTES, timeMinutes);
		intent.putExtra(FAILSAFE_ALARM_HOURS, failsafeHours);
		intent.putExtra(FAILSAFE_ALARM_MINUTES, failsafeMinutes);
		
		startActivity(intent);
	}

}
