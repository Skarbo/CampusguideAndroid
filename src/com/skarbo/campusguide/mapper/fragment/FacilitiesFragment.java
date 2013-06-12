package com.skarbo.campusguide.mapper.fragment;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

import com.skarbo.campusguide.mapper.BuildingActivity;
import com.skarbo.campusguide.mapper.MainActivity;
import com.skarbo.campusguide.mapper.R;
import com.skarbo.campusguide.mapper.adapter.FacilityListAdapter;
import com.skarbo.campusguide.mapper.handler.CampusguideHandler;
import com.skarbo.campusguide.mapper.model.Building;
import com.skarbo.campusguide.mapper.model.Facility;
import com.skarbo.campusguide.mapper.model.adapter.ModelAdapter;

public class FacilitiesFragment extends Fragment {

	protected static final String TAG = FacilitiesFragment.class.getSimpleName();

	private CampusguideHandler campusguideHandler;
	private ExpandableListView facilitiesExpandableListView;
	private FacilityListAdapter facilityListAdapter;

	DataSetObserver facilitiesObserver = new DataSetObserver() {
		@Override
		public void onChanged() {
			Log.d(TAG, "Facilities changed");
			doRefresh();
		}
	};

	DataSetObserver buildingsObserver = new DataSetObserver() {
		@Override
		public void onChanged() {
			Log.d(TAG, "Buildings changed");
			doRefresh();
		}
	};

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// CAMPUSGUIDE HANDLER

		this.campusguideHandler = ((MainActivity) getActivity()).getCampusguideHandler();
		this.campusguideHandler.getFacilities().registerDataSetObserver(facilitiesObserver);
		this.campusguideHandler.getBuildings().registerDataSetObserver(buildingsObserver);

		// /CAMPUSGUIDE HANDLER

		this.facilityListAdapter = new FacilityListAdapter(getActivity(), campusguideHandler.getFacilities());
		this.facilitiesExpandableListView.setAdapter(this.facilityListAdapter);

	}

	@Override
	public void onResume() {
		super.onResume();
		doRefresh();
	}

	@Override
	public void onPause() {
		super.onPause();

		// CAMPUSGUIDE HANDLER

		this.campusguideHandler.getFacilities().unregisterDataSetObserver(facilitiesObserver);
		this.campusguideHandler.getBuildings().unregisterDataSetObserver(buildingsObserver);

		// /CAMPUSGUIDE HANDLER
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_facilities, null);

		facilitiesExpandableListView = (ExpandableListView) view.findViewById(R.id.facilitiesExpandableListView);

		facilitiesExpandableListView.setOnChildClickListener(new OnChildClickListener() {
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				Building building = (Building) facilityListAdapter.getChild(groupPosition, childPosition);

				if (building != null) {
					Intent buildingIntent = new Intent(getActivity(), BuildingActivity.class);
					buildingIntent.putExtra(BuildingActivity.ARG_BUILDING_ID, building.getId());
					startActivity(buildingIntent);
					return true;
				}
				return false;
			}
		});

		return view;
	}

	private void doRefresh() {
		for (Facility facility : campusguideHandler.getFacilities()) {
			((Facility) facility).getBuildings().clear();
			ModelAdapter<Building> buildings = campusguideHandler.getBuildings().getForeign(facility.getId());
			for (Building building : buildings) {
				((Facility) facility).getBuildings().add((Building) building);
			}
		}
		this.facilityListAdapter.notifyDataSetChanged();
	}

}
