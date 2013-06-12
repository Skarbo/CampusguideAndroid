package com.skarbo.campusguide.mapper;

import java.util.ArrayList;

import android.database.DataSetObserver;
import android.graphics.PointF;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.actionbarsherlock.view.Window;
import com.actionbarsherlock.widget.SearchView;
import com.skarbo.campusguide.mapper.dao.web.StandardWebDao.StandardWebCallback;
import com.skarbo.campusguide.mapper.dao.web.StandardWebDao.StandardWebResult;
import com.skarbo.campusguide.mapper.handler.CampusguideHandler;
import com.skarbo.campusguide.mapper.model.Building;
import com.skarbo.campusguide.mapper.model.Element;
import com.skarbo.campusguide.mapper.model.Facility;
import com.skarbo.campusguide.mapper.model.Floor;
import com.skarbo.campusguide.mapper.model.adapter.ModelAdapter;
import com.skarbo.campusguide.mapper.view.ViewportView;
import com.skarbo.campusguide.mapper.view.map.BuildingMapView;

public class BuildingActivity extends SherlockActivity {

	public static final String ARG_BUILDING_ID = "building_id";

	private static final String TAG = BuildingActivity.class.getSimpleName();

	// private TextView buildingNameTextView;

	private CampusguideHandler campusguideHandler;
	private int buildingId = 0;
	private GestureDetector gestureDetector;

	private PointF start = new PointF();
	private float oldDist = 0f;
	private PointF mid = new PointF();

	private ViewportView viewportView;
	private BuildingMapView buildingMapView;
	private RelativeLayout loadingLayout;
	private RelativeLayout buildingMapFloorsRelativeLayout;
	private RelativeLayout buildingMapLayout;

	private ArrayAdapter<String> buildingMapFloorsListAdapter;

	DataSetObserver elementsObserver = new DataSetObserver() {
		@Override
		public void onChanged() {
			Log.d(TAG, "Elements changed");
			doRefreshBuildingMap();
		}
	};

	DataSetObserver floorsObserver = new DataSetObserver() {
		@Override
		public void onChanged() {
			Log.d(TAG, "Floors changed");
			doRefreshBuildingMap();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// ACTIONBAR

		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("Building");
		actionBar.setSubtitle("Facility");
		actionBar.setIcon(R.drawable.map);

		// /ACTIONBAR

		// CAMPUSGUIDE HANDLER

		int mode = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(this).getString("server_mode", "1"));
		this.campusguideHandler = new CampusguideHandler(getApplicationContext(), mode);

		this.campusguideHandler.getFloors().registerDataSetObserver(floorsObserver);
		this.campusguideHandler.getElements().registerDataSetObserver(elementsObserver);

		// /CAMPUSGUIDE HANDLER

		// CONTENT

		setContentView(R.layout.activity_building);

		this.loadingLayout = (RelativeLayout) findViewById(R.id.buildingLoadingRelativeLayout);
		this.buildingMapLayout = (RelativeLayout) findViewById(R.id.buildingMapRelativeLayout);
		this.viewportView = (ViewportView) findViewById(R.id.viewportView);
		this.buildingMapView = (BuildingMapView) findViewById(R.id.buildingMap);

		this.buildingMapFloorsRelativeLayout = (RelativeLayout) findViewById(R.id.buildingMapFloorsRelativeLayout);
		Button buildingMapZoomInButton = (Button) findViewById(R.id.buildingMapZoomInButton);
		Button buildingMapZoomOutButton = (Button) findViewById(R.id.buildingMapZoomOutButton);
		buildingMapZoomInButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				viewportView.doZoom(true);
			}
		});
		buildingMapZoomOutButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				viewportView.doZoom(false);
			}
		});

		ListView buildingMapFloorsListView = (ListView) findViewById(R.id.buildingMapFloorsListView);
		this.buildingMapFloorsListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
				new ArrayList<String>());
		buildingMapFloorsListView.setAdapter(buildingMapFloorsListAdapter);
		buildingMapFloorsListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				doFloorSwitch(arg2);
			}
		});

		// CONTENT

		this.buildingId = getIntent().getIntExtra(ARG_BUILDING_ID, 0);

	}

	@Override
	protected void onResume() {
		super.onResume();

		// this.campusguideHandler.getBuildingProxy().retrieve(this.buildingId,
		// new StandardWebCallback<Building>() {
		// public void onResult(StandardWebResult<Building> result, boolean
		// isUpdated) {
		// if (isUpdated)
		// doRefresh();
		//
		// if (result.getSingle() != null) {
		// campusguideHandler.getFacilityProxy().retrieve(result.getSingle().getFacilityId(),
		// new StandardWebCallback<Facility>() {
		// public void onResult(StandardWebResult<Facility> result, boolean
		// isUpdated) {
		// if (isUpdated)
		// doRefresh();
		// }
		// });
		// }
		// }
		// });
		// this.campusguideHandler.getFloorProxy().retrieveForeign(new Integer[]
		// { this.buildingId },
		// new StandardWebCallback<Floor>() {
		// public void onResult(StandardWebResult<Floor> result, boolean
		// isUpdated) {
		// Integer[] floorIds = result.getList().getIds();
		// doRefreshBuildingMap();
		//
		// campusguideHandler.getElementProxy().retrieveForeign(floorIds,
		// new StandardWebCallback<Element>() {
		// public void onResult(StandardWebResult<Element> result, boolean
		// isUpdated) {
		// doRefreshBuildingMap();
		// }
		// });
		// }
		// });

		this.campusguideHandler.retrieveBuildingcreator(this.buildingId, null, new CampusguideHandler.Type[] {
				CampusguideHandler.Type.Elements, CampusguideHandler.Type.Navigations });

		doRefresh();
		doRefreshBuildingMap();
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		this.campusguideHandler.getFloors().unregisterDataSetObserver(floorsObserver);
		this.campusguideHandler.getElements().unregisterDataSetObserver(elementsObserver);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.campusguideHandler.closeDatabase();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// SEARCH

		SearchView searchView = new SearchView(getSupportActionBar().getThemedContext());
		searchView.setQueryHint("Search campus");

		// /SEARCH

		// MENU

		menu.add("Search").setIcon(R.drawable.search).setActionView(searchView)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		MenuItem locationMenu = menu.add("Location");
		locationMenu.setIcon(R.drawable.location).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		locationMenu.setEnabled(false);

		// /MENU

		// menu.add("Refresh").setIcon(android.R.drawable.ic_)
		// .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS |
		// MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		SubMenu subMenu1 = menu.addSubMenu("More");
		subMenu1.add(1, 1, 0, "Settings");
		subMenu1.add("About");
		subMenu1.add("Help");

		MenuItem subMenu1Item = subMenu1.getItem();
		subMenu1Item.setIcon(R.drawable.overflow);
		subMenu1Item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	private void doRefresh() {
		Log.d(TAG, "Do refresh, building: " + this.buildingId);
		Building building = this.campusguideHandler.getBuildingProxy().get(this.buildingId);
		if (building != null) {
			// this.buildingNameTextView.setText(this.building.getName());
			getSupportActionBar().setTitle(building.getName());

			Facility facility = this.campusguideHandler.getFacilityProxy().get(building.getFacilityId());
			if (facility != null) {
				getSupportActionBar().setSubtitle(facility.getName());
			}
		}
	}

	private void doRefreshBuildingMap() {
		this.loadingLayout.setVisibility(View.GONE);
		this.buildingMapLayout.setVisibility(View.VISIBLE);

		// Get Floors
		ModelAdapter<Floor> floors = this.campusguideHandler.getFloorProxy().getForeign(this.buildingId);

		// Main floor
		if (floors.isEmpty()) {
			Log.w(TAG, "DoRefreshBuildingMap: Empty floors");
			return;
		}

		Floor mainFloor = floors.get(0);
		for (Floor floor : floors) {
			if (floor.isMain())
				mainFloor = floor;
		}
		doFloorSwitch(floors.indexOf(mainFloor));
		// buildingMapView.resetZoom();

		this.buildingMapFloorsListAdapter.clear();
		for (Floor floor : floors) {
			this.buildingMapFloorsListAdapter.add(String.valueOf(floor.getOrder()));
		}
		this.buildingMapFloorsListAdapter.notifyDataSetChanged();
	}

	protected void doFloorSwitch(int floorIndex) {
		if (this.campusguideHandler.getFloors().size() <= floorIndex)
			return;
		Floor floor = this.campusguideHandler.getFloors().get(floorIndex);
		Log.d(TAG, "DoFloorSwitch: " + floorIndex + ", " + floor.getId());

		ModelAdapter<Element> floorElements = this.campusguideHandler.getElementProxy().getForeign(floor.getId());

		buildingMapView.loadFloor(floor, floorElements);
		// buildingMapView.loadElements(floorElements);
		// buildingMapView.invalidate();
		// ViewCompat.postInvalidateOnAnimation(buildingMapView);
		buildingMapView.requestLayout();
	}

	public void doFloorsPickerToggle() {
		Log.d(TAG, "DoFloorsPickerToggle: " + buildingMapFloorsRelativeLayout.getVisibility());
		if (buildingMapFloorsRelativeLayout.getVisibility() == View.GONE) {
			buildingMapFloorsRelativeLayout.setVisibility(View.VISIBLE);
		} else {
			buildingMapFloorsRelativeLayout.setVisibility(View.GONE);
		}
	}

}
