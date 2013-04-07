package com.example.smartalarm;

import android.os.Bundle;
import android.preference.PreferenceActivity;

// https://developer.android.com/guide/topics/ui/settings.html
public class SettingsActivity extends PreferenceActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}
