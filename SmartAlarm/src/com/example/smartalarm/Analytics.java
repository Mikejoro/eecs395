package com.example.smartalarm;

import java.util.*;

/*
 * Clock Recovery algorithm to detect sleep cycle.
 */
public class Analytics {
	public static float ClockRecover(float[] points, float freq) {
		int N = points.length;
		float min_var = Float.MAX_VALUE;
		float final_med = 0;
		for (float offs : new float[]{0.f, .25f, .5f, .75f})
		{
			float[] modpoints = points.clone();
			float var = 0, avg = 0;
			
			for (int i = 0; i < N; ++i)
			{
				modpoints[i] = (modpoints[i] + offs) % freq;
				avg += modpoints[i];
				var += modpoints[i] * modpoints[i];
			}
			avg /= N;
			var /= N;
			var -= avg * avg;
			
			if (var > min_var) //don't sort unless we have to
				continue;
			
			min_var = var;
			Arrays.sort(points);
			if ((N & 1) == 1) //if odd
				final_med = modpoints[N/2];
			else
				final_med = (modpoints[N/2 - 1] + modpoints[N/2])/2.f;
		}
		return final_med;
	}
	
	/**
	 * Fast (n log(n) ish) Discrete Cosine Transform.
	 * @param data Data array, may be of any length, but the more composite the faster.
	 * @return Array of same length
	 */
	public static float[] FDCT(float[] data) {
		float[] out = new float[data.length]; //initialized to zeroes
		int N, len, num, shift = 0;
		len = N = data.length;
		
		//halve it as much as possible
		while ((len & 1) == 0) {
			len /= 2;
			++shift;
		}
		shift = 32 - shift;
		num = N / len;
		
		//do standard n^2 DCT on indivisible case
		//the positions have to be rearranged to bit-reversed positions so the Cooley-Tukey algorithm can be performed in place
		for (int b = 0; b < num; ++b)
			for (int k = 0; k < len; ++k)
				for (int n = 0; n < len; ++n) //Do the Harlem Shake...
					out[Integer.reverse(b + num * k) >>> shift] += data[b + num * n] //WOB WOB WOB
					    * (float)Math.cos(Math.PI / (double)len * ((double)n + .5) * (double)k);
		
		//do fast DCT
		for (; len != N; len *= 2, num /= 2) {
			for (int b = 0; b < num; b += 2) {
				for (int k = 0; k < len; ++k) {
					float ltr = out[b * len + k];
					float rtl = out[(b + 1) * len + k] * (float)Math.cos(Math.PI * .5 * (double)k / (double)len);
					out[(b + 1) * len + k] *= -(float)Math.sin(Math.PI * .5 * (double)k / (double)len);
					out[b * len + k] += rtl;
					out[(b + 1) * len + k] += ltr;
				}
			}
		}
		
		return out;
	}
}
