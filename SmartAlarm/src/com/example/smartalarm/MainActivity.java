package com.example.smartalarm;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


//Right now the commented out section is from the previous way of writing to file. In next push it will be changed to 
//using the SQLite database in DatabaseHandler
public class MainActivity extends Activity implements SensorEventListener {
	private float mLastX, mLastY, mLastZ;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private boolean mInitialized;
	private final float NOISE = (float) 0.25;	//can be changed/removed
	private FileOutputStream fos;
	private final String FILE_NAME = "SmartAlarm_Data";
	private FileInputStream fis;
//	private String text = "";		
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	try {fos = openFileOutput(FILE_NAME, Context.MODE_APPEND);} 
    		catch (FileNotFoundException e) {e.printStackTrace();}
    	/*test cost to make sure things are being put in file correctly
    	try {fis = openFileInput(FILE_NAME);} 
		catch (FileNotFoundException e) {e.printStackTrace();}
    	/**/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mInitialized = false;
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this,  mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	
    	mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	
    	mSensorManager.unregisterListener(this);
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	
    	try {
			fos.close();
		} catch (IOException e) {e.printStackTrace();}
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
    	//ImageView iv = (ImageView) findViewById(R.id.image);
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
//    		if(deltaX > 0 || deltaY > 0 || deltaZ > 0) {
//    			String toWrite = 
//    		    		dateFormat.format(date) + "-" + deltaX + ","
//    					+ deltaY + "," + deltaZ;
//    			try {
//					fos.write(toWrite.getBytes());
//				} catch (IOException e) {e.printStackTrace();}
//    		}
    		/*this code will read the file we created
    		if(text.equals("")) {		
    			try {    	  
    				int content;
    				while ((content = fis.read()) != -1) {
    					// convert to char and display it
    					//remember to delete count var
    					text += (char) content;
    				}
    	 
    			} catch (IOException e) {
    				e.printStackTrace();
    			} finally {
    				try {
    					System.out.println(text);
    					if (fis != null)
    						fis.close();
    				} catch (IOException ex) {
    					ex.printStackTrace();
    				}
    			}
    		}

			tvFile.setText(text);
    		/**/
    	}
    }  
}
