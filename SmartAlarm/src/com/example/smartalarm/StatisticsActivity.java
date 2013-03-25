package com.example.smartalarm;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.os.Build;
import com.jjoe64.graphview.*;
import com.jjoe64.graphview.GraphView.GraphViewData;

public class StatisticsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_statistics);
		
		GraphViewSeries exampleSeries = new GraphViewSeries(new GraphViewData[] {
				new GraphViewData(1, 2.0d)
				, new GraphViewData(2, 1.5d)
				, new GraphViewData(2.5, 3.0d) // another frequency
				, new GraphViewData(3, 2.5d)
				, new GraphViewData(4, 1.0d)
				, new GraphViewData(5, 3.0d)
		});

		// graph with dynamically genereated horizontal and vertical labels
		GraphView graphView;

			graphView = new LineGraphView(
					this // context
					, "GraphViewDemo" // heading
			);
		
		graphView.addSeries(exampleSeries); // data

		LinearLayout layout = (LinearLayout) findViewById(R.id.graph1);
		layout.addView(graphView);

		// graph with custom labels and drawBackground

			graphView = new LineGraphView(
					this
					, "GraphViewDemo"
			);
			((LineGraphView) graphView).setDrawBackground(true);
		
		// custom static labels
		graphView.setHorizontalLabels(new String[] {"2 days ago", "yesterday", "today", "tomorrow"});
		graphView.setVerticalLabels(new String[] {"high", "middle", "low"});
		graphView.addSeries(exampleSeries); // data

		layout = (LinearLayout) findViewById(R.id.graph2);
		layout.addView(graphView);
		
		
		// Show the Up button in the action bar.
		setupActionBar();
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.statistics, menu);
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
