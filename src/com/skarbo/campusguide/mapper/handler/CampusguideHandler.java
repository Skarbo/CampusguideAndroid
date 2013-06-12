package com.skarbo.campusguide.mapper.handler;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.skarbo.campusguide.mapper.container.BuildingContainer;
import com.skarbo.campusguide.mapper.dao.proxy.BuildingProxyDao;
import com.skarbo.campusguide.mapper.dao.proxy.ElementProxyDao;
import com.skarbo.campusguide.mapper.dao.proxy.FacilityProxyDao;
import com.skarbo.campusguide.mapper.dao.proxy.FloorProxyDao;
import com.skarbo.campusguide.mapper.dao.proxy.NavigationProxyDao;
import com.skarbo.campusguide.mapper.model.Building;
import com.skarbo.campusguide.mapper.model.Element;
import com.skarbo.campusguide.mapper.model.Facility;
import com.skarbo.campusguide.mapper.model.Floor;
import com.skarbo.campusguide.mapper.model.Navigation;
import com.skarbo.campusguide.mapper.model.adapter.ModelAdapter;
import com.skarbo.campusguide.mapper.util.RestClient;
import com.skarbo.campusguide.mapper.util.SQLiteHelper;
import com.skarbo.campusguide.mapper.util.Util;
import com.skarbo.campusguide.mapper.util.RestClient.RequestMethod;

public class CampusguideHandler {

	public enum Type {
		Elements, Navigations
	};

	private static final String TAG = CampusguideHandler.class.getSimpleName();

	private static final String URI = "http://%s:8008/KrisSkarbo/CampusGuide/api_rest.php?/";
	private static final String URI_BUILDINGCREATOR = URI + "buildingcreator/%s/%s/%s&mode=%d";
	private static final String URI_ID_SPLITTER = "_";

	private Context context;
	private int mode;
	private SQLiteHelper dbHelper;
	private String serverIp;

	private FacilityProxyDao facilityDao;
	private BuildingProxyDao buildingDao;
	private FloorProxyDao floorDao;
	private ElementProxyDao elementDao;
	private NavigationProxyDao navigationDao;

	public CampusguideHandler(Context context, int mode) {
		this.context = context;
		this.mode = mode;
		this.dbHelper = new SQLiteHelper(context, mode);
		this.serverIp = PreferenceManager.getDefaultSharedPreferences(context).getString("server_ip", "");

		SQLiteDatabase database = openDatabase();

		this.facilityDao = new FacilityProxyDao(context, database, mode);
		this.buildingDao = new BuildingProxyDao(context, database, mode);
		this.floorDao = new FloorProxyDao(context, database, mode);
		this.elementDao = new ElementProxyDao(context, database, mode);
	}

	public SQLiteDatabase openDatabase() {
		try {
			Log.d(TAG, "Opening database");
			return dbHelper.getWritableDatabase();
		} catch (IllegalStateException e) {
			Log.e(TAG, e.getMessage());
		}
		return null;
	}

	public void closeDatabase() {
		Log.d(TAG, "Closing database");
		try {
			dbHelper.close();
		} catch (Exception e) {
			Log.w(TAG, e.getMessage());
		}
	}

	public FacilityProxyDao getFacilityProxy() {
		return facilityDao;
	}

	public BuildingProxyDao getBuildingProxy() {
		return buildingDao;
	}

	public FloorProxyDao getFloorProxy() {
		return floorDao;
	}

	public ElementProxyDao getElementProxy() {
		return elementDao;
	}

	private NavigationProxyDao getNavigationProxy() {
		return navigationDao;
	}

	public ModelAdapter<Facility> getFacilities() {
		return facilityDao.getList();
	}

	public ModelAdapter<Building> getBuildings() {
		return buildingDao.getList();
	}

	public ModelAdapter<Floor> getFloors() {
		return floorDao.getList();
	}

	public ModelAdapter<Element> getElements() {
		return elementDao.getList();
	}

	public ModelAdapter<Navigation> getNavigations() {
		return navigationDao.getList();
	}

	public void retrieveBuildingcreator(int buildingId, Integer[] floorIds, Type[] types) {
		new RetrieveBuildingcreatorAsync().execute(String.format(URI_BUILDINGCREATOR, this.serverIp, buildingId,
				Util.implode(floorIds, URI_ID_SPLITTER), Util.implode(types, URI_ID_SPLITTER), mode));
	}

	// CLASS

	public class RetrieveBuildingcreatorAsync extends AsyncTask<String, Void, BuildingContainer> {

		@Override
		protected BuildingContainer doInBackground(String... arg0) {
			if (arg0.length > 0) {
				RestClient client = new RestClient(arg0[0]);
				try {
					client.execute(RequestMethod.GET);
					return BuildingContainer.generate(client.getResponse());
				} catch (Exception e) {
					Log.e(TAG, "RetrieveBuildingcreatorAsync.doInBackground: " + e.getMessage());
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(BuildingContainer result) {
			if (result != null) {
				if (result.building != null) {
					getBuildingProxy().getStandardDbDao().add(result.building);
					getBuildingProxy().getList().addModel(result.building);
				}
				if (result.elements != null && !result.elements.isEmpty()) {
					getElementProxy().getStandardDbDao().addAll(result.elements);
					getElementProxy().getList().addModels(result.elements);
				}
				if (result.floors != null && !result.floors.isEmpty()) {
					getFloorProxy().getStandardDbDao().addAll(result.floors);
					getFloorProxy().getList().addModels(result.floors);
				}
				if (result.navigations != null && !result.navigations.isEmpty()) {
					getNavigationProxy().getStandardDbDao().addAll(result.navigations);
					getNavigationProxy().getList().addModels(result.navigations);
				}
			} else {
				Log.e(TAG, "RetrieveBuildingcreatorAsync.onPostExecute: Result is null");
			}
		}

	}

	// /CLASS

}
