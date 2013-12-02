package com.example.uiuccampusevents;

import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class DetailActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		
		Intent intent = getIntent();
		String url = intent.getStringExtra(MainActivity.URL_MESSAGE);
		
		TextView titleView = (TextView)findViewById(R.id.titleText);
		TextView dateLabel = (TextView)findViewById(R.id.dateLabel);
		TextView dateView = (TextView)findViewById(R.id.dateText);
		TextView timeLabel = (TextView)findViewById(R.id.timeLabel);
		TextView timeView = (TextView)findViewById(R.id.timeText);
		TextView sponsorLabel = (TextView)findViewById(R.id.sponsorLabel);
		TextView sponsorView = (TextView)findViewById(R.id.sponsorText);
		TextView locationLabel = (TextView)findViewById(R.id.locationLabel);
		TextView locationView = (TextView)findViewById(R.id.locationText);
		TextView costLabel = (TextView)findViewById(R.id.costLabel);
		TextView costView = (TextView)findViewById(R.id.costText);
		TextView descLabel = (TextView)findViewById(R.id.descLabel);
		TextView descView = (TextView)findViewById(R.id.descText);
		String[][] result = new String[1][1];
		result[0][0] = "Nothing found";
		
		try {
			result = new HtmlDownloader().execute(url).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		titleView.setText(result[0][0]);
		dateLabel.setTypeface(null,Typeface.BOLD);
		dateView.setText(result[0][1]);
		timeLabel.setTypeface(null,Typeface.BOLD);
		timeView.setText(result[0][2]);
		sponsorLabel.setTypeface(null,Typeface.BOLD);
		sponsorView.setText(result[0][3]);
		locationLabel.setTypeface(null,Typeface.BOLD);
		locationView.setText(result[0][4]);
		costLabel.setTypeface(null,Typeface.BOLD);
		costView.setText(result[0][8]);
		descLabel.setTypeface(null,Typeface.BOLD);
		descView.setText(result[0][9]);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.detail, menu);
		return true;
	}

}
