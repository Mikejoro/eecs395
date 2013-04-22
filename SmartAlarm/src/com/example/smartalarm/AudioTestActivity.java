package com.example.smartalarm;

import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.os.Build;

public class AudioTestActivity extends Activity {
	
	LinkedBlockingQueue<AudioDataPoint> queue = new LinkedBlockingQueue<AudioDataPoint>();
	AudioThread audTh;
	Thread updateTh;
	GraphView graphView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_audio_test);
		graphView = new LineGraphView(
				this // context
				, "Audio" // heading
		);
		LinearLayout layout = (LinearLayout) findViewById(R.id.audioGraph);
		GraphViewSeries ser = new GraphViewSeries(
				new GraphViewData[]{new GraphViewData(0, -1), new GraphViewData(10, 1)});
		graphView.addSeries(ser);
		layout.addView(graphView);
	}
	
	@Override
	protected void onStart() {
		audTh = new AudioThread(queue);
		audTh.start();
		
		final Handler hnd = new Handler() {
			public void handleMessage(Message msg) {
				try {
					AudioDataPoint dp;
					dp = queue.take();
					queue.clear();
					double[] fft = Analytics.FFT(dp.data);
					
					GraphViewData[] points = new GraphViewData[fft.length/2];
					for (int i=0; i<fft.length/2; ++i)
						points[i] = new GraphViewData(i, fft[i]);
					GraphViewSeries ser = new GraphViewSeries(points);
					graphView.removeSeries(0);
					graphView.addSeries(ser);
					graphView.invalidate();
				} catch (InterruptedException e) {}
			}
		};
		
		updateTh = new Thread() {
			public void run() {
				try {
					while (true) {
						sleep(300);
						hnd.sendEmptyMessage(0);
					}
				} catch (InterruptedException e) {}
			}
		};
		updateTh.start();
		super.onStart();
	}
	
	@Override
	protected void onStop() {
		audTh.close();
		updateTh.interrupt();
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.audio_test, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
