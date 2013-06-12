package com.skarbo.campusguide.mapper;

import java.util.Arrays;

import org.apache.cordova.api.LOG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.actionbarsherlock.view.Window;
import com.actionbarsherlock.widget.SearchView;
import com.skarbo.campusguide.mapper.dao.web.StandardWebDao.StandardWebCallback;
import com.skarbo.campusguide.mapper.dao.web.StandardWebDao.StandardWebResult;
import com.skarbo.campusguide.mapper.fragment.FacilitiesFragment;
import com.skarbo.campusguide.mapper.fragment.MapFragment;
import com.skarbo.campusguide.mapper.handler.CampusguideHandler;
import com.skarbo.campusguide.mapper.model.Building;
import com.skarbo.campusguide.mapper.model.Facility;

public class MainActivity extends SherlockFragmentActivity implements OnNavigationListener {

	public enum ViewController {
		MAP, FACILITIES
	};

	// public static final String IP = "192.168.0.103"; //"192.168.0.106"; //
	// "10.0.0.43";
	// public static final int MODE = 2;
	static final String TAG = "Facilities";

	private CampusguideHandler campusguideHandler;
	private boolean isTwoPanel;

	private MapFragment mapFragment;
	private FacilitiesFragment facilitiesFragment;
	private SubMenu subMenu1;
	private Fragment mContent;
	private MenuItem locationMenu;

	// ... ON

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		// Actionbar
		final ActionBar actionBar = getSupportActionBar();

		// CONTENT

		setContentView(R.layout.frame_content);

		// Set actionbar title
		actionBar.setTitle("Map");

		// Fragments
		mapFragment = new MapFragment();
		facilitiesFragment = new FacilitiesFragment();

		// Is two panel
		isTwoPanel = findViewById(R.id.menu_frame) != null;

		if (savedInstanceState != null)
			mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
		if (mContent == null)
			mContent = mapFragment;

		if (isTwoPanel) {
			// Content frame
			getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mapFragment).commit();

			// Secondary frame
			getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame, facilitiesFragment).commit();

			// Actionbar
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			actionBar.setDisplayShowTitleEnabled(true);
		} else {
			// Content frame
			getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mContent).commit();

			// Spinner dropdown
			Context context = actionBar.getThemedContext();
			ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource(context, R.array.menu_list,
					R.layout.spinner_item);
			list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

			// Actionbar
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
			actionBar.setListNavigationCallbacks(list, this);
			actionBar.setDisplayShowTitleEnabled(false);
		}

		// /CONTENT

		// Hide progress spinner
		setSupportProgressBarIndeterminateVisibility(false);

		int mode = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(this).getString("server_mode", "1"));
		campusguideHandler = new CampusguideHandler(getApplicationContext(), mode);

//		 Intent buildingIntent = new Intent(this, BuildingActivity.class);
//		 buildingIntent.putExtra(BuildingActivity.ARG_BUILDING_ID, 645); // 
//		 startActivity(buildingIntent);

		// SLIDE MENU

		// if (!isTwoPanel) {
		// setBehindContentView(R.layout.frame_menu);
		// getSlidingMenu().setSlidingEnabled(true);
		// getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		// actionBar.setDisplayHomeAsUpEnabled(true);
		// } else {
		// View v = new View(this);
		// setBehindContentView(v);
		// getSlidingMenu().setSlidingEnabled(false);
		// getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		// actionBar.setDisplayHomeAsUpEnabled(false);
		// }

		// // Set the Above View Fragment
		// getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
		// mapFragment).commit();

		// Set the Behind View Fragment
		// getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame,
		// new MenuFragment()).commit();

		// // Customize the SlidingMenu
		// SlidingMenu sm = getSlidingMenu();
		// sm.setFadeDegree(0.35f);
		// sm.setShadowWidthRes(R.dimen.shadow_width);
		// sm.setShadowDrawable(R.drawable.shadow);
		// sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);

		// /SLIDE MENU

		// this.expandableListView = (ExpandableListView)
		// findViewById(R.id.expandableListView1);
		// this.facilities = new ArrayList<Facility>();// generateFacilities();
		// this.facilityListAdapter = new FacilityListAdapter(this,
		// this.facilities);
		// this.expandableListView.setAdapter(facilityListAdapter);
		//
		// FacilitiesRetrieveTask facilitiesRetrieveTask = new
		// FacilitiesRetrieveTask();
		// facilitiesRetrieveTask.execute();
		//
		// this.expandableListView.setOnGroupExpandListener(new
		// OnGroupExpandListener() {
		// public void onGroupExpand(int groupPosition) {
		// Facility facility = (Facility)
		// facilityListAdapter.getGroup(groupPosition);
		// Log.d(TAG, "Group expand: " + groupPosition + ", " + (facility !=
		// null));
		// if (facility != null) {
		// if (facility.getBuildings().size() != facility.getBuildingsCount()) {
		// Log.d(TAG, "Retrieve buildings");
		// BuildingsRetrieveTask buildingsRetrieveTask = new
		// BuildingsRetrieveTask();
		// buildingsRetrieveTask.execute(facility.getId());
		// }
		// }
		// }
		// });
		//
		// this.expandableListView.setOnChildClickListener(new
		// OnChildClickListener() {
		// public boolean onChildClick(ExpandableListView parent, View v, int
		// groupPosition, int childPosition, long id) {
		// Building building = (Building)
		// facilityListAdapter.getChild(groupPosition, childPosition);
		// Log.d(TAG, "Child click: " + groupPosition + ", " + childPosition +
		// ", " + building.getName());
		// Intent buildingIntent = new Intent(getApplicationContext(),
		// BuildingActivity.class);
		// buildingIntent.putExtra(BuildingActivity.ARG_BUILDING_ID,
		// building.getId());
		// startActivity(buildingIntent);
		// return true;
		// }
		// });
	}

	@Override
	protected void onResume() {
		super.onResume();

		SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		if (!defaultSharedPreferences.contains("server_mode") || !defaultSharedPreferences.contains("server_ip")) {
			doPreferences();
			return;
		}

		// CAMPUSGUIDE HANDLER

		campusguideHandler.getFacilityProxy().retrieveAll(new StandardWebCallback<Facility>() {
			public void onResult(StandardWebResult<Facility> result, boolean isUpdated) {
				Integer[] facilityIds = result.getList().getIds();
				if (facilityIds.length > 0) {
					campusguideHandler.getBuildingProxy().retrieveForeign(facilityIds, null);
				}
			}
		});

		// /CAMPUSGUIDE HANDLER
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// Campusguide handler
		this.campusguideHandler.closeDatabase();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, "mContent", mContent);
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
		locationMenu = menu.add("Location");
		locationMenu.setIcon(R.drawable.location).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		locationMenu.setEnabled(false);

		// /MENU

		// menu.add("Refresh").setIcon(android.R.drawable.ic_)
		// .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS |
		// MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		subMenu1 = menu.addSubMenu("More");
		subMenu1.add(1, 1, 0, "Settings");
		subMenu1.add("About");
		subMenu1.add("Help");

		MenuItem subMenu1Item = subMenu1.getItem();
		subMenu1Item.setIcon(R.drawable.overflow);
		subMenu1Item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		return super.onCreateOptionsMenu(menu);
	}

	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		switch (itemPosition) {
		case 0:
			doSwitchViewController(ViewController.MAP);
			return true;
		case 1:
			doSwitchViewController(ViewController.FACILITIES);
			return true;
		}
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "OnOptionsItemSelected: " + item.getItemId());
		if (item.getGroupId() == 1) {
			if (item.getItemId() == 1) {
				doPreferences();
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}

	// ... /ON

	// ... GET

	public CampusguideHandler getCampusguideHandler() {
		return campusguideHandler;
	}

	public MenuItem getLocationMenu() {
		return locationMenu;
	}

	// ... /GET

	// ... DO

	public void doSwitchViewController(ViewController viewController) {
		switch (viewController) {
		case MAP:
			mContent = mapFragment;
			break;
		case FACILITIES:
			mContent = facilitiesFragment;
			break;
		}

		if (mContent != null && !isFinishing()) {
			getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mContent).commit();
			// getSlidingMenu().showContent();

			// if (viewController == ViewController.MAP) {
			// getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
			// } else {
			// getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
			// }
			// if (isTwoPanel) {
			// getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
			// }
		}
	}

	private void doPreferences() {
		Intent prefIntent = new Intent(this, PreferenceActivity.class);
		startActivity(prefIntent);
	}

	// ... /DO

	// ... CLASS

	// class FacilitiesRetrieveTask extends AsyncTask<Integer, Integer,
	// List<Facility>> {
	//
	// @Override
	// protected void onPreExecute() {
	// super.onPreExecute();
	// // progressBar.setVisibility(View.VISIBLE);
	// setSupportProgressBarIndeterminateVisibility(true);
	// expandableListView.setVisibility(View.GONE);
	// }
	//
	// @Override
	// protected List<Facility> doInBackground(Integer... params) {
	// Integer[] ids = new Integer[params.length];
	// for (int i = 0; i < params.length; i++)
	// ids[i] = params[i];
	// List<Facility> facilities = null;
	// try {
	// facilities = RestHandler.retrieveFacilities(ids);
	// } catch (Exception e) {
	// cancel(true);
	// e.printStackTrace();
	// }
	// return facilities;
	// }
	//
	// @Override
	// protected void onPostExecute(List<Facility> result) {
	// super.onPostExecute(result);
	// // progressBar.setVisibility(View.GONE);
	// setSupportProgressBarIndeterminateVisibility(false);
	// expandableListView.setVisibility(View.VISIBLE);
	// for (Facility facility : result) {
	// facilityListAdapter.addGroup(facility);
	// }
	// facilityListAdapter.notifyDataSetChanged();
	// }
	//
	// }
	//
	// class BuildingsRetrieveTask extends AsyncTask<Integer, Integer,
	// List<Building>> {
	//
	// @Override
	// protected void onPreExecute() {
	// super.onPreExecute();
	// setSupportProgressBarIndeterminateVisibility(true);
	// // progressBar.setVisibility(View.VISIBLE);
	// }
	//
	// @Override
	// protected List<Building> doInBackground(Integer... params) {
	// Integer[] ids = new Integer[params.length];
	// for (int i = 0; i < params.length; i++)
	// ids[i] = params[i];
	// List<Building> buildings = null;
	// try {
	// buildings = RestHandler.retrieveBuildings(ids);
	// } catch (Exception e) {
	// cancel(true);
	// e.printStackTrace();
	// }
	// return buildings;
	// }
	//
	// @Override
	// protected void onPostExecute(List<Building> result) {
	// super.onPostExecute(result);
	// setSupportProgressBarIndeterminateVisibility(false);
	// // progressBar.setVisibility(View.GONE);
	// for (Building building : result) {
	// facilityListAdapter.addItem(building, building.getFacilityId());
	// }
	// facilityListAdapter.notifyDataSetChanged();
	// }
	//
	// }

}

// public class MainActivity extends DroidGap {
//
// public static final String IP = "192.168.0.105:8008";
//
// @Override
// public void onCreate(Bundle savedInstanceState) {
// super.onCreate(savedInstanceState);
// // super.loadUrl("file:///android_asset/www/index.html");
// // super.loadUrl("http://" + IP +
// // "/KrisSkarbo/CampusGuide/test_phonegap.html");
// super.loadUrl("http://" + IP + "/KrisSkarbo/CampusGuide/app.php?mode=2 ");
// }
// }
