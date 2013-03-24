package com.example.smartalarm;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ConfirmSleepAlarmActivity extends Activity {

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

}
