package com.example.uiuccampusevents;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class EventArrayAdapter extends ArrayAdapter<String[]> {
	private final Context context;
	private final String[][] values;

	public EventArrayAdapter(Context context, String[][] values) {
		super(context, R.layout.event_item, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.event_item, parent, false);
		
		//Set text views for each item in the list
		TextView textTitle = (TextView) rowView.findViewById(R.id.title);
		TextView textMonth = (TextView) rowView.findViewById(R.id.month);
		TextView textDay = (TextView) rowView.findViewById(R.id.day);
		TextView textTime = (TextView) rowView.findViewById(R.id.time);
		
		//Sets the text to be inserted in each text view
		textTitle.setText(values[position][0]);
		textTitle.setTypeface(null,Typeface.BOLD);
		textMonth.setText(values[position][2]);
		textDay.setText(values[position][3]);
		textTime.setText(values[position][1]);
		
		if (position % 2 == 0) rowView.setBackgroundColor(0x30e0ecf8);
		else rowView.setBackgroundColor(0x30ffffff);
		
		return rowView;
	}
}