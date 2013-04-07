package com.example.smartalarm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.os.Bundle;
import android.provider.Settings;
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
		TimePicker timepicker = (TimePicker) findViewById(R.id.alarmTimePicker);
		TimePicker failsafePicker = (TimePicker) findViewById(R.id.alarmFailsafePicker);
		SimpleDateFormat sdf = new SimpleDateFormat();
		Date defaultDate;
		try {
			defaultDate = sdf.parse(Settings.System.NEXT_ALARM_FORMATTED);
			Date early = (Date) defaultDate.clone();
			early.setTime(early.getTime() - 1000 * 60 * 30);
			timepicker.setCurrentHour(early.getHours());
			timepicker.setCurrentMinute(early.getMinutes());
			failsafePicker.setCurrentHour(defaultDate.getHours());
			failsafePicker.setCurrentMinute(defaultDate.getMinutes());
		} catch (ParseException e) {
			timepicker.setCurrentHour(6);
			timepicker.setCurrentMinute(30);
			failsafePicker.setCurrentHour(7);
			failsafePicker.setCurrentMinute(0);
			e.printStackTrace();
		}
		
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
