package com.skarbo.campusguide.mapper;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class FacilitiesActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.facilities);

		String[] mStrings = { "String1", "String2", "String3" };
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mStrings ));
		getListView().setTextFilterEnabled(true);
	}

}
