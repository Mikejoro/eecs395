package com.example.smartalarm;

public class AccelDataPoint {

	public long time;
	public float[] data;
	
	public AccelDataPoint(long time, float[] data) {
		this.time = time;
		this.data = data;
	}
}
