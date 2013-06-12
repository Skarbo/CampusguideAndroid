package com.skarbo.campusguide.mapper.dao.db;

import org.apache.cordova.api.LOG;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.skarbo.campusguide.mapper.dao.ElementDao;
import com.skarbo.campusguide.mapper.model.Element;
import com.skarbo.campusguide.mapper.model.Element.ElementFactory;
import com.skarbo.campusguide.mapper.model.adapter.ModelAdapter;
import com.skarbo.campusguide.mapper.util.SQLiteHelper;

public class ElementDbDao extends StandardDbDao<Element> implements ElementDao {

	private static final String TAG = ElementDbDao.class.getSimpleName();
	private ElementFactory elementFactory;

	public ElementDbDao(SQLiteDatabase database) {
		super(database);
		elementFactory = new Element.ElementFactory();
	}

	@Override
	protected String getTable() {
		return SQLiteHelper.ElementSql.TABLE;
	}

	public ModelAdapter<Element> getBuilding(int buildingId) {
		return null;
	}

	@Override
	protected String getColumnForeign() {
		return SQLiteHelper.ElementSql.COLUMN_FLOOR_ID;
	}

	@Override
	protected Element createModel(Cursor cursor) {
		return elementFactory.generate(cursor);
	}

	@Override
	protected ContentValues createAddEditValues(Element single) {
		ContentValues values = new ContentValues();

		values.put(SQLiteHelper.ElementSql.COLUMN_ID, single.getId());
		values.put(SQLiteHelper.ElementSql.COLUMN_FLOOR_ID, single.getFloorId());
		values.put(SQLiteHelper.ElementSql.COLUMN_NAME, single.getName());
		values.put(SQLiteHelper.ElementSql.COLUMN_TYPE, single.getType());
		values.put(SQLiteHelper.ElementSql.COLUMN_TYPE_GROUP, single.getTypeGroup());
		values.put(SQLiteHelper.ElementSql.COLUMN_UPDATED, single.getUpdated());

		try {
			Gson gson = new Gson();
			String dataString = gson.toJson(single.getCoordinates()); //Coordinate.toDataString(single.getCoordinates());
			values.put(SQLiteHelper.ElementSql.COLUMN_COORDINATES, dataString);			
		} catch (Exception e) {
			LOG.e(TAG, "createAddEditValues: " + e.getMessage());
		}

		return values;
	}

}
