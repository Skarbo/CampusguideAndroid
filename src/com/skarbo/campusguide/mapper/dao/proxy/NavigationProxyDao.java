package com.skarbo.campusguide.mapper.dao.proxy;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.skarbo.campusguide.mapper.dao.NavigationDao;
import com.skarbo.campusguide.mapper.dao.db.NavigationDbDao;
import com.skarbo.campusguide.mapper.dao.db.StandardDbDao;
import com.skarbo.campusguide.mapper.model.Navigation;
import com.skarbo.campusguide.mapper.model.Factory;

public class NavigationProxyDao extends StandardProxyDao<Navigation> implements NavigationDao {

	public NavigationProxyDao(Context context, SQLiteDatabase database, int mode) {
		super(context, database, mode);
	}

	@Override
	protected Factory<Navigation> getFactory() {
		return new Navigation.NavigationFactory();
	}

	@Override
	protected String getController() {
		return "buildingnavigation";
	}

	@Override
	public StandardDbDao<Navigation> getStandardDbDao() {
		return new NavigationDbDao(this.database);
	}

}
