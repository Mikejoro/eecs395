package com.example.smartalarm;

import java.util.*;

/*
 * Clock Recovery algorithm to detect sleep cycle.
 */
public class Analytics {
	public static long ClockRecover(List<Long> points, long period) {
		int N = points.size();
		long min_var = Long.MAX_VALUE;
		long final_med = 0;
		for (long offs : new long[]{0, period / 4, period / 2, period * 3 / 4})
		{
			long[] modpoints = new long[N];
			long var = 0, avg = 0;
			
			for (int i = 0; i < N; ++i)
			{
				modpoints[i] = (points.get(i) + offs) % period;
				avg += modpoints[i];
				var += modpoints[i] * modpoints[i];
			}
			avg /= N;
			var /= N;
			var -= avg * avg;
			
			if (var > min_var) //don't sort unless we have to
				continue;
			
			min_var = var;
			Arrays.sort(modpoints);
			if ((N & 1) == 1) //if odd
				final_med = modpoints[N/2];
			else
				final_med = (modpoints[N/2 - 1] + modpoints[N/2])/2;
		}
		return final_med;
	}
	
	/**
	 * Fast (n log(n) ish) Fourier Transform.
	 * @param data Data array, may be of any length, but the more composite the faster.
	 * @return Array of same length
	 */
	public static float[] FFT(float[] data) {
		float[] re = new float[data.length]; //initialized to zeroes
		float[] im = new float[data.length]; //initialized to zeroes
		int N, len, num, shift = 0;
		len = N = data.length;
		
		//halve it as much as possible
		while ((len & 1) == 0) {
			len /= 2;
			++shift;
		}
		shift = 32 - shift;
		num = N / len;
		
		//do standard n^2 FFT on indivisible case
		//the positions have to be rearranged to bit-reversed positions so the Cooley-Tukey algorithm can be performed in place
		for (int b = 0; b < num; ++b)
			for (int k = 0; k < len; ++k)
				for (int n = 0; n < len; ++n) { //Do the Harlem Shake...
					re[b * len + k] += data[(Integer.reverse(b) >>> shift) + n * num] //WOB WOB WOB
					    * (float)Math.cos(2 * Math.PI / (double)len * (double)n * (double)k);
					im[b * len + k] += data[(Integer.reverse(b) >>> shift) + n * num]
					    * -(float)Math.sin(2 * Math.PI / (double)len * (double)n * (double)k);
				}
		
		//do fast FFT
		for (; len != N; len *= 2, num /= 2) {
			for (int b = 0; b < num; b += 2) {
				for (int k = 0; k < len; ++k) {
					//hur da durr i'm java and i don't believe in operator overloading
					int l = b * len + k, r = l + len;
					double phi = Math.PI * (double)k / (double)len;
					float re_ltr = re[l];
					float im_ltr = im[l];
					float re_rtl =  re[r] * (float)Math.cos(phi) + im[r] * (float)Math.sin(phi);
					float im_rtl = -re[r] * (float)Math.sin(phi) + im[r] * (float)Math.cos(phi);
					re[r] = re_ltr - re_rtl;
					im[r] = im_ltr - im_rtl;
					re[l] = re_ltr + re_rtl;
					im[l] = im_ltr + im_rtl;
				}
			}
		}
		
		return re;
	}
}
