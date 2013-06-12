package com.skarbo.campusguide.mapper.dao.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.skarbo.campusguide.mapper.dao.FacilityDao;
import com.skarbo.campusguide.mapper.model.Facility;
import com.skarbo.campusguide.mapper.model.adapter.ModelAdapter;
import com.skarbo.campusguide.mapper.util.SQLiteHelper;

public class FacilityDbDao extends StandardDbDao<Facility> implements FacilityDao {

	public FacilityDbDao(SQLiteDatabase database) {
		super(database);
	}

	@Override
	protected String getTable() {
		return SQLiteHelper.FacilitySql.TABLE;
	}

	@Override
	protected Facility createModel(Cursor cursor) {
		Facility.FacilityFactory facilityFactory = new Facility.FacilityFactory();
		return facilityFactory.generate(cursor);
	}

	@Override
	public ModelAdapter<Facility> getForeign(Integer[] foreignIds) {
		return getAll();
	}

	@Override
	protected ContentValues createAddEditValues(Facility single) {
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.FacilitySql.COLUMN_ID, single.getId());
		values.put(SQLiteHelper.FacilitySql.COLUMN_NAME, single.getName());
		values.put(SQLiteHelper.FacilitySql.COLUMN_BUILDING_COUNT, single.getBuildingsCount());
		values.put(SQLiteHelper.FacilitySql.COLUMN_UPDATED, single.getUpdated());
		return values;
	}

}
