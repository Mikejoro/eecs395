package com.example.smartalarm;

import java.util.*;

/*
 * Clock Recovery algorithm to detect sleep cycle.
 */
public class Analytics {
	public float ClockRecover(float[] points, float freq) {
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
}
