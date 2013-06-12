package com.skarbo.campusguide.mapper.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.skarbo.campusguide.mapper.MainActivity;
import com.skarbo.campusguide.mapper.MainActivity.ViewController;
import com.skarbo.campusguide.mapper.R;

public class MenuFragment extends ListFragment {

	private static final String TAG = MenuFragment.class.getSimpleName();
	private MainActivity mainActivity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.list, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		String[] menuList = { "Map", "Facilities" };
		ArrayAdapter<String> menuAdapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, android.R.id.text1, menuList);
		setListAdapter(menuAdapter);
		mainActivity = (MainActivity) getActivity();
	}

	@Override
	public void onListItemClick(ListView lv, View v, int position, long id) {
		if (mainActivity == null)
			return;
		
		switch (position) {
		case 0:
			mainActivity.doSwitchViewController(ViewController.MAP);
			break;

		case 1:
			mainActivity.doSwitchViewController(ViewController.FACILITIES);
			break;
		}
	}

}