package com.example.smartalarm;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

// Triggered by SensorService when it's time to wake up
public class AlarmActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sleep_mode);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.button_Sleep:
				startActivity(new Intent(this, ConfirmSleepAlarmActivity.class));
				break;
			case R.id.button_ShowStats:
				startActivity(new Intent(this, StatisticsActivity.class));
				break;
		}
	}

}
