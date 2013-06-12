package com.skarbo.campusguide.mapper.dao.proxy;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.skarbo.campusguide.mapper.dao.BuildingDao;
import com.skarbo.campusguide.mapper.dao.db.BuildingDbDao;
import com.skarbo.campusguide.mapper.dao.db.StandardDbDao;
import com.skarbo.campusguide.mapper.model.Building;
import com.skarbo.campusguide.mapper.model.Factory;

public class BuildingProxyDao extends StandardProxyDao<Building> implements BuildingDao {

	public BuildingProxyDao(Context context, SQLiteDatabase database, int mode) {
		super(context, database, mode);
	}

	@Override
	protected Factory<Building> getFactory() {
		return new Building.BuildingFactory();
	}

	@Override
	protected String getController() {
		return "buildings";
	}

	@Override
	public StandardDbDao<Building> getStandardDbDao() {
		return new BuildingDbDao(this.database);
	}

}
