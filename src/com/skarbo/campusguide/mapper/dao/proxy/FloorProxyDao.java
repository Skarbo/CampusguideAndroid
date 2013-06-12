package com.skarbo.campusguide.mapper.dao.proxy;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.skarbo.campusguide.mapper.dao.FloorDao;
import com.skarbo.campusguide.mapper.dao.db.FloorDbDao;
import com.skarbo.campusguide.mapper.dao.db.StandardDbDao;
import com.skarbo.campusguide.mapper.dao.web.FloorWebDao;
import com.skarbo.campusguide.mapper.dao.web.rest.FloorRestWebDao;
import com.skarbo.campusguide.mapper.model.Factory;
import com.skarbo.campusguide.mapper.model.Floor;
import com.skarbo.campusguide.mapper.model.adapter.ModelAdapter;

public class FloorProxyDao extends StandardProxyDao<Floor> implements FloorDao, FloorWebDao {

	public FloorProxyDao(Context context, SQLiteDatabase database, int mode) {
		super(context, database, mode);
		this.standardRestDao = new FloorRestWebDao(context, mode, new Floor.FloorFactory());
	}

	@Override
	protected Factory<Floor> getFactory() {
		return new Floor.FloorFactory();
	}

	@Override
	protected String getController() {
		return "buildingfloors";
	}

	@Override
	public StandardDbDao<Floor> getStandardDbDao() {
		return new FloorDbDao(this.database);
	}

	public ModelAdapter<Floor> getMain(Integer[] buildingIds) {
		ModelAdapter<Floor> list = ((FloorDbDao) this.standardDbDao).getMain(buildingIds);
		modelAdapter.addModels(list);
		return modelAdapter;
	}

	public void retrieveMain(Integer[] buildingIds, final StandardWebCallback<Floor> callback) {
		((FloorRestWebDao) standardRestDao).retrieveMain(buildingIds, new StandardWebCallback<Floor>() {
			public void onResult(StandardWebResult<Floor> result, boolean isUpdated) {
				standardDbDao.addAll(result.getList());
				boolean updated = modelAdapter.addModels(result.getList());
				if (callback != null)
					callback.onResult(result, updated);
			}
		});
	}

}
