package com.example.smartalarm;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/* mike wrote this comment*/

//Right now the commented out section is from the previous way of writing to file. In next push it will be changed to 
//using the SQLite database in DatabaseHandler
public class MainActivity extends Activity implements SensorEventListener {
	private float mLastX, mLastY, mLastZ;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private boolean mInitialized;
	private final float NOISE = (float) 0.25;	//can be changed/removed
	private DatabaseHandler db;
	private SharedPreferences prefs;
//	private String text = "";		
	
	public void onButtonSleepClick(View view) {   
    	// Do something in response to button}
    	Intent intent = new Intent(this, ConfirmSleepAlarmActivity.class);
    	//EditText editText = (EditText) findViewById(R.id.edit_message);
    	//String message = editText.getText().toString();
    	//intent.putExtra(EXTRA_MESSAGE, message);
    	startActivity(intent);
    }
	public void onButtonStatsClick(View view) {
		Intent intent = new Intent(this, StatisticsActivity.class);
		startActivity(intent);
	}
	public void onButtonSettingsClick(View view) {
		startActivity(new Intent(this, SettingsActivity.class));
	}
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mInitialized = false;
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // PreferenceManager example
        prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
    	if (prefs.getBoolean("ck_accel", true)) {
    		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    	}
        db = new DatabaseHandler(this);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	// PreferenceManager example
    	if (prefs.getBoolean("ck_accel", true)) {
    		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    	}
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	
    	mSensorManager.unregisterListener(this);
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	
//    	try {
//			fos.close();
//		} catch (IOException e) {e.printStackTrace();}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    	//can be ignored for now
    }
    
    @Override
    public void onSensorChanged(SensorEvent event) {
    	TextView tvX = (TextView) findViewById(R.id.x_axis);
    	TextView tvY = (TextView) findViewById(R.id.y_axis);
    	TextView tvZ = (TextView) findViewById(R.id.z_axis);
//    	TextView tvFile = (TextView) findViewById(R.id.read_file);
    	float x = event.values[0];	//x value from accelerometer
    	float y = event.values[1];	//y value
    	float z = event.values[2];	//z value
    	if (!mInitialized){
    		mLastX = x;
    		mLastY = y;
    		mLastZ = z;
    		tvX.setText("0.0");
    		tvY.setText("0.0");
    		tvZ.setText("0.0");
    		mInitialized = true;
    	}
    	else {
    		float deltaX = Math.abs(mLastX - x);
    		float deltaY = Math.abs(mLastY - y);
    		float deltaZ = Math.abs(mLastZ - z);
    		if(deltaX < NOISE) deltaX = (float) 0.0;
    		if(deltaY < NOISE) deltaY = (float) 0.0;
    		if(deltaZ < NOISE) deltaZ = (float) 0.0;
    		
    		mLastX = x;
    		mLastY = y;
    		mLastZ = z;
    		
    		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd:HH:mm:ss:SS\n");
    		Date date = new Date();

    		tvX.setText(Float.toString(x));
    		tvY.setText(Float.toString(y));
    		tvZ.setText(Float.toString(z));
    		if(deltaX > 0 || deltaY > 0 || deltaZ > 0) {
    			
    			String timeStamp = dateFormat.format(date);
    			Log.d("Insert: ", "Inserting ..");
    			db.addDataPoint(new DataPoint(x, y, z, timeStamp));
    		}
    		else {
    			List<DataPoint> dp = db.readDataPoint("2013/02/18", "2013/02/18");
    			Log.d("Reading: ", Integer.toString(dp.size()));
    			for(DataPoint d: dp) {
    				String log = "Date: " + d.getTimeStamp() + ", X = " + d.getX() +
    						", Y = " + d.getY() + ", Z = " + d.getZ();
    				Log.d(":: ", log);
    			}
    		}
//			tvFile.setText(text);
    		/**/
    	}
    }  
}
