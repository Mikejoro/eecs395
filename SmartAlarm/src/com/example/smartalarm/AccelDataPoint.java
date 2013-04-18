package com.example.smartalarm;

public class AccelDataPoint {

	public long time;
	public float[] data;
	
	public AccelDataPoint(long time, float[] data) {
		this.time = time;
		this.data = data;
	}
	
	/*
	 * struct AccelDataPoint {
	 *     uint64 delta_time;
	 *     float32 delta_x, delta_y, delta_z;
	 * };
	 * 
	 */
	public byte[] serialize() {
		int x = Float.floatToRawIntBits(data[0]);
		int y = Float.floatToRawIntBits(data[1]);
		int z = Float.floatToRawIntBits(data[2]);
		return new byte[]{
			(byte)((time >> 56) & 0xff),
			(byte)((time >> 48) & 0xff),
			(byte)((time >> 40) & 0xff),
			(byte)((time >> 32) & 0xff),
			(byte)((time >> 24) & 0xff),
			(byte)((time >> 16) & 0xff),
			(byte)((time >> 8) & 0xff),
			(byte)((time >> 0) & 0xff),
			(byte)((x >> 24) & 0xff),
			(byte)((x >> 16) & 0xff),
			(byte)((x >> 8) & 0xff),
			(byte)((x >> 0) & 0xff),
			(byte)((y >> 24) & 0xff),
			(byte)((y >> 16) & 0xff),
			(byte)((y >> 8) & 0xff),
			(byte)((y >> 0) & 0xff),
			(byte)((z >> 24) & 0xff),
			(byte)((z >> 16) & 0xff),
			(byte)((z >> 8) & 0xff),
			(byte)((z >> 0) & 0xff),
		};
	}
}
