package com.skarbo.campusguide.mapper.dao.db;

import java.util.Arrays;

import org.apache.cordova.api.LOG;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;
import com.skarbo.campusguide.mapper.dao.FloorDao;
import com.skarbo.campusguide.mapper.model.Floor;
import com.skarbo.campusguide.mapper.model.Floor.FloorFactory;
import com.skarbo.campusguide.mapper.model.adapter.ModelAdapter;
import com.skarbo.campusguide.mapper.util.Coordinate;
import com.skarbo.campusguide.mapper.util.SQLiteHelper;
import com.skarbo.campusguide.mapper.util.Util;

public class FloorDbDao extends StandardDbDao<Floor> implements FloorDao {

	private static final String TAG = FloorDbDao.class.getSimpleName();
	private FloorFactory floorFactory;

	public FloorDbDao(SQLiteDatabase database) {
		super(database);
		floorFactory = new Floor.FloorFactory();
	}

	@Override
	protected String getTable() {
		return SQLiteHelper.FloorSql.TABLE;
	}

	@Override
	protected String getColumnForeign() {
		return SQLiteHelper.FloorSql.COLUMN_BUILDING_ID;
	}

	@Override
	protected Floor createModel(Cursor cursor) {
		return floorFactory.generate(cursor);
	}

	@Override
	protected ContentValues createAddEditValues(Floor single) {
		ContentValues values = new ContentValues();

		values.put(SQLiteHelper.FloorSql.COLUMN_ID, single.getId());
		values.put(SQLiteHelper.FloorSql.COLUMN_BUILDING_ID, single.getBuildingId());
		values.put(SQLiteHelper.FloorSql.COLUMN_NAME, single.getName());
		values.put(SQLiteHelper.FloorSql.COLUMN_MAIN, single.isMain());
		values.put(SQLiteHelper.FloorSql.COLUMN_ORDER, single.getOrder());
		values.put(SQLiteHelper.FloorSql.COLUMN_UPDATED, single.getUpdated());

		try {
			Gson gson = new Gson();
			String dataString = gson.toJson(single.getCoordinates());
			values.put(SQLiteHelper.FloorSql.COLUMN_COORDINATES, dataString);			
		} catch (Exception e) {
			Log.e(TAG, "createAddEditValues: " + e.getMessage());
		}

		return values;
	}

	public ModelAdapter<Floor> getMain(Integer[] buildingIds) {
		if (buildingIds.length == 0)
			return createList(null);

		String[] buildingIdsString = new String[buildingIds.length];
		for (int i = 0; i < buildingIds.length; i++)
			buildingIdsString[i] = String.valueOf(buildingIds[i]);

		String[] selections = new String[buildingIdsString.length];
		Arrays.fill(selections, String.format("%s=?", getColumnForeign()));
		String selection = String.format("%s=1 AND (%s)", SQLiteHelper.FloorSql.COLUMN_MAIN,
				Util.implode(selections, " OR "));

		Cursor cursor = database.query(getTable(), getColumnsAll(), selection, buildingIdsString, null, null,
				SQLiteHelper.FloorSql.COLUMN_ORDER);

		return createList(cursor);
	}

}
