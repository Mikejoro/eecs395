package com.example.smartalarm;

public class DataPoint {
	int id;
	float x, y, z;
	String timeStamp;
	
	public DataPoint()	{}
	
	public DataPoint(float xP, float yP, float zP, String date_stamp){
		x = xP;
		y = yP;
		z = zP;
		timeStamp = date_stamp;
	}
	
	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}

	public float getZ() {
		return this.z;
	}
	
	public String getTimeStamp() {
		return this.timeStamp;
	}
}
