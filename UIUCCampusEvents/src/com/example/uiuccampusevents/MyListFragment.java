package com.example.uiuccampusevents;

import java.util.concurrent.ExecutionException;

import android.os.Bundle;
import android.support.v4.app.ListFragment;

public class MyListFragment extends ListFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
			 
		String[][] result = new String[1][4];
		//List<String> events = new ArrayList<String>();
		try {
			result = new HtmlDownloader().execute("http://illinois.edu/calendar/list/7").get();
			/*for (int i=0; i < result.length; i++){
				events.add(result[i]);
			}*/
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		//ArrayAdapter<String> array = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, events);
		EventArrayAdapter array = new EventArrayAdapter(getActivity(), result);
		setListAdapter(array);
	}

}
