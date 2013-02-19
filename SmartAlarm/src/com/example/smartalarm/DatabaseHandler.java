package com.example.smartalarm;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {
	 
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "sleepData";
 
    // Contacts table name
    private static final String TABLE_DATA = "data";
 
    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_DELTA_X = "deltaX";
    private static final String KEY_DELTA_Y = "deltaY";
    private static final String KEY_DELTA_Z = "deltaZ";
    private static final String KEY_DATE = "date";
 
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_DATA_TABLE = "CREATE TABLE " + TABLE_DATA + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DELTA_X + " REAL," + KEY_DELTA_Y + " REAL,"
                + KEY_DELTA_Z + " REAL," + KEY_DATE + " TEXT" + ")";
        db.execSQL(CREATE_DATA_TABLE);
    }
 
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA);
 
        // Create tables again
        onCreate(db);
    }
    
    public void addDataPoint(DataPoint dp)
    {
    	
    	SQLiteDatabase db = this.getWritableDatabase();
    	ContentValues values = new ContentValues();
    	
    	values.put(KEY_DELTA_X, dp.getX());
    	values.put(KEY_DELTA_Y, dp.getY());
    	values.put(KEY_DELTA_Z, dp.getZ());
    	values.put(KEY_DATE, dp.getTimeStamp());
    	
    	//Insert into table
    	db.insert(TABLE_DATA, null, values);
    	db.close();
    }
    
    public List<DataPoint> readDataPoint(String startDate, String endDate)
    {
    	SQLiteDatabase db = this.getReadableDatabase();
    	 
    	//right now this gets an exact date, will be changed to date range
//    	String dateQuery = "SELECT " + KEY_DELTA_X + ", " + KEY_DELTA_X + ", " + 
//    			KEY_DELTA_X + ", " + KEY_DATE + " FROM " + TABLE_DATA + " WHERE " +
//    			KEY_DATE + " <= " + endDate + " AND " + KEY_DATE + " >= " + startDate;
    	String dateQuery = "SELECT * FROM " + TABLE_DATA;
    	
        Cursor cursor = db.rawQuery(dateQuery, null);
    	List<DataPoint> dpList = new ArrayList();
    	 // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
            	dpList.add(new DataPoint(Float.parseFloat(cursor.getString(1)), Float.parseFloat(cursor.getString(2)), 
            			Float.parseFloat(cursor.getString(3)), cursor.getString(4)));
            } while (cursor.moveToNext());
        }
        db.close();
        return dpList;
    }    
}
