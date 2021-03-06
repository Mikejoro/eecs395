package com.example.smartalarm;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public void onResume() {
		super.onResume();
		Button btn = (Button) findViewById(R.id.button_Sleep);
		if (SensorService.isRunning(this)) {
			btn.setText(getString(R.string.stop_service));
		} else {
			btn.setText(getString(R.string.button_Sleep));
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.button_Sleep:
				Button btn = (Button) findViewById(R.id.button_Sleep);
				if (SensorService.isRunning(this)) {
					SensorService.stop(this);
					btn.setText(getString(R.string.button_Sleep));
				} else {
					startActivity(new Intent(this, ConfirmSleepAlarmActivity.class));
				}
				break;
			case R.id.button_ShowStats:
				startActivity(new Intent(this, StatisticsActivity.class));
				break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
			case R.id.menu_settings:
				startActivity(new Intent(this, SettingsActivity.class));
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

}
