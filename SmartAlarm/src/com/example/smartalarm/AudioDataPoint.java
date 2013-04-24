package com.example.smartalarm;

public class AudioDataPoint {

	public long time;
	public float[] data;
	
	public AudioDataPoint(long time, float[] data) {
		this.time = time;
		this.data = data;
	}
}
