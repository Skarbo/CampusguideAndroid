package com.skarbo.campusguide.mapper.dao.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;
import com.skarbo.campusguide.mapper.dao.BuildingDao;
import com.skarbo.campusguide.mapper.model.Building;
import com.skarbo.campusguide.mapper.model.adapter.ModelAdapter;
import com.skarbo.campusguide.mapper.util.SQLiteHelper;
import com.skarbo.campusguide.mapper.util.Util;

public class BuildingDbDao extends StandardDbDao<Building> implements BuildingDao {

	private static final String ADDRESS_SPLITTER = "|";
	private static final String LOCATION_SPLITTER = ",";
	private static final String POSITION_SPLITTER = ",";
	private static final String OVERLAY_SPLITTER = ",";
	private static final String TAG = BuildingDbDao.class.getSimpleName();

	public BuildingDbDao(SQLiteDatabase database) {
		super(database);
	}

	@Override
	protected String getTable() {
		return SQLiteHelper.BuildingSql.TABLE;
	}

	@Override
	protected String getColumnForeign() {
		return SQLiteHelper.BuildingSql.COLUMN_FACILITY_ID;
	}

	@Override
	protected Building createModel(Cursor cursor) {
		Building.BuildingFactory buildingFactory = new Building.BuildingFactory();
		return buildingFactory.generate(cursor);
	}

	@Override
	public ModelAdapter<Building> getForeign(Integer[] foreignIds) {
		return getAll();
	}

	@Override
	protected ContentValues createAddEditValues(Building single) {
		ContentValues values = new ContentValues();

		Gson gson = new Gson();

		values.put(SQLiteHelper.BuildingSql.COLUMN_ID, single.getId());
		values.put(SQLiteHelper.BuildingSql.COLUMN_FACILITY_ID, single.getFacilityId());
		values.put(SQLiteHelper.BuildingSql.COLUMN_NAME, single.getName());
		values.put(SQLiteHelper.BuildingSql.COLUMN_FLOOR_COUNT, single.getFloors());
		values.put(SQLiteHelper.BuildingSql.COLUMN_UPDATED, single.getUpdated());

		try {
			values.put(SQLiteHelper.BuildingSql.COLUMN_ADDRESS, gson.toJson(single.getAddress()));
		} catch (Exception e) {
			Log.e(TAG, "createAddEditValues: " + e.getMessage());
		}
		try {
			values.put(SQLiteHelper.BuildingSql.COLUMN_LOCATION, gson.toJson(single.getLocation()));
		} catch (Exception e) {
			Log.e(TAG, "createAddEditValues: " + e.getMessage());
		}
		try {
			values.put(SQLiteHelper.BuildingSql.COLUMN_POSITION, gson.toJson(single.getPosition()));
		} catch (Exception e) {
			Log.e(TAG, "createAddEditValues: " + e.getMessage());
		}
		try {
			values.put(SQLiteHelper.BuildingSql.COLUMN_OVERLAY, gson.toJson(single.getOverlay()));
		} catch (Exception e) {
			Log.e(TAG, "createAddEditValues: " + e.getMessage());
		}

		return values;
	}

}
