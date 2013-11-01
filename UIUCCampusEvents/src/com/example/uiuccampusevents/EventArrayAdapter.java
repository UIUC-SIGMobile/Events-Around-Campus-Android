package com.example.uiuccampusevents;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class EventArrayAdapter extends ArrayAdapter<String> {
	private final Context context;
	private final String[] values;

	public EventArrayAdapter(Context context, String[] values) {
		super(context, R.layout.event_item, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.event_item, parent, false);
		TextView textTitle = (TextView) rowView.findViewById(R.id.title);
		TextView textMonth = (TextView) rowView.findViewById(R.id.month);
		TextView textDay = (TextView) rowView.findViewById(R.id.day);
		TextView textTime = (TextView) rowView.findViewById(R.id.time);
		textTitle.setText(values[position]);
		textTitle.setTypeface(null,Typeface.BOLD);
		textMonth.setText("Jan");
		textDay.setText("01");
		textTime.setText("9:00-12:00");
		return rowView;
	}
}