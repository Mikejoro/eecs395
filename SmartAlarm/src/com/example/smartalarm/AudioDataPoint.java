package com.example.smartalarm;

public class AudioDataPoint {

	public long time;
	public short[] data;
	
	public AudioDataPoint(long time, short[] data) {
		this.time = time;
		this.data = data;
	}
}
