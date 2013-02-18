package com.example.smartalarm;

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
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DELTA_X + " TEXT," + KEY_DELTA_Y + " TEXT,"
                + KEY_DELTA_Z + " TEXT," + KEY_DATE + " TEXT" + ")";
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
    
    public void addDataPoint(String dX, String dY, String dZ, String time_stamp)
    {
    	SQLiteDatabase db = this.getWritableDatabase();
    	ContentValues values = new ContentValues();
    	
    	values.put(KEY_DELTA_X, dX);
    	values.put(KEY_DELTA_Y, dY);
    	values.put(KEY_DELTA_Z, dZ);
    	
    	//Insert into table
    	db.insert(TABLE_DATA, null, values);
    	db.close();
    }
    
    public void readDataPoint(String date_range)
    {
    	SQLiteDatabase db = this.getReadableDatabase();
    	 
    	//right now this gets an exact date, will be changed to date range
        Cursor cursor = db.query(TABLE_DATA, new String[] { KEY_ID,
                KEY_DELTA_X, KEY_DELTA_Y, KEY_DELTA_Z, KEY_DATE }, KEY_DATE + "=?",
                new String[] { date_range }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        
        float x, y, z;	//temporary storage, will be replaced with object probably
        x = Float.parseFloat(cursor.getString(1));
        y = Float.parseFloat(cursor.getString(2));
        z = Float.parseFloat(cursor.getString(3));
        String date_storage = cursor.getString(4);
    }    
}
