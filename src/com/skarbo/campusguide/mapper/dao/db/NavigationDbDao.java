package com.skarbo.campusguide.mapper.dao.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;
import com.skarbo.campusguide.mapper.dao.NavigationDao;
import com.skarbo.campusguide.mapper.model.Navigation;
import com.skarbo.campusguide.mapper.model.Navigation.NavigationFactory;
import com.skarbo.campusguide.mapper.util.SQLiteHelper;

public class NavigationDbDao extends StandardDbDao<Navigation> implements NavigationDao {

	private static final String TAG = NavigationDbDao.class.getSimpleName();
	private NavigationFactory navigationFactory;

	public NavigationDbDao(SQLiteDatabase database) {
		super(database);
		navigationFactory = new Navigation.NavigationFactory();
	}

	@Override
	protected String getTable() {
		return SQLiteHelper.NavigationSql.TABLE;
	}

	@Override
	protected String getColumnForeign() {
		return SQLiteHelper.NavigationSql.COLUMN_FLOOR_ID;
	}

	@Override
	protected Navigation createModel(Cursor cursor) {
		return navigationFactory.generate(cursor);
	}

	@Override
	protected ContentValues createAddEditValues(Navigation single) {
		ContentValues values = new ContentValues();

		values.put(SQLiteHelper.NavigationSql.COLUMN_ID, single.getId());
		values.put(SQLiteHelper.NavigationSql.COLUMN_FLOOR_ID, single.getFloorId());
		values.put(SQLiteHelper.NavigationSql.COLUMN_ELEMENT_ID, single.getElementId());
		values.put(SQLiteHelper.NavigationSql.COLUMN_UPDATED, single.getUpdated());

		try {
			Gson gson = new Gson();
			String dataString = gson.toJson(single.getCoordinate());
			values.put(SQLiteHelper.NavigationSql.COLUMN_COORDINATE, dataString);
		} catch (Exception e) {
			Log.e(TAG, "createAddEditValues: " + e.getMessage());
		}

		return values;
	}

}
