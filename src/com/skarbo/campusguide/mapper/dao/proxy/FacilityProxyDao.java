package com.skarbo.campusguide.mapper.dao.proxy;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.skarbo.campusguide.mapper.dao.FacilityDao;
import com.skarbo.campusguide.mapper.dao.db.FacilityDbDao;
import com.skarbo.campusguide.mapper.dao.db.StandardDbDao;
import com.skarbo.campusguide.mapper.model.Facility;
import com.skarbo.campusguide.mapper.model.Factory;

public class FacilityProxyDao extends StandardProxyDao<Facility> implements FacilityDao {

	public FacilityProxyDao(Context context, SQLiteDatabase database, int mode) {
		super(context, database, mode);
	}

	@Override
	protected Factory<Facility> getFactory() {
		return new Facility.FacilityFactory();
	}

	@Override
	protected String getController() {
		return "facilities";
	}

	@Override
	public StandardDbDao<Facility> getStandardDbDao() {
		return new FacilityDbDao(this.database);
	}

}
