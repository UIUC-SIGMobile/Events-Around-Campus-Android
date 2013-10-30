package com.example.uiuccampusevents;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.widget.ArrayAdapter;

public class MyListFragment extends ListFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		String[] s = {"a", "b", "c", "d", "e"};
		List<String> blah = new ArrayList<String>();
		for (int i=0; i < s.length; i++){
			blah.add(s[i]);
		}
		ArrayAdapter<String> array = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, blah);
		setListAdapter(array);
	}

}
