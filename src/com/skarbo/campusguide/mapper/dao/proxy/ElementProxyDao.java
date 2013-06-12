package com.skarbo.campusguide.mapper.dao.proxy;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.skarbo.campusguide.mapper.dao.ElementDao;
import com.skarbo.campusguide.mapper.dao.db.ElementDbDao;
import com.skarbo.campusguide.mapper.dao.db.StandardDbDao;
import com.skarbo.campusguide.mapper.dao.web.ElementWebDao;
import com.skarbo.campusguide.mapper.dao.web.rest.ElementRestWebDao;
import com.skarbo.campusguide.mapper.model.Element;
import com.skarbo.campusguide.mapper.model.Factory;
import com.skarbo.campusguide.mapper.model.adapter.ModelAdapter;

public class ElementProxyDao extends StandardProxyDao<Element> implements ElementDao, ElementWebDao {

	public ElementProxyDao(Context context, SQLiteDatabase database, int mode) {
		super(context, database, mode);
		this.standardRestDao = new ElementRestWebDao(context, mode);
	}

	@Override
	protected Factory<Element> getFactory() {
		return new Element.ElementFactory();
	}

	@Override
	protected String getController() {
		return "buildingelements";
	}

	@Override
	public StandardDbDao<Element> getStandardDbDao() {
		return new ElementDbDao(this.database);
	}

	public ModelAdapter<Element> getBuilding(int buildingId) {
		return null;
	}

	// public void getBuilding(final int buildingId, final
	// ListStandardDaoCallback<Element> callback) {
	// ((ElementRestDao) this.standardRestDao).getBuilding(buildingId, new
	// ListStandardDaoCallback<Element>() {
	// public void onList(ModelAdapter<Element> list) {
	// modelAdapter.addModels(list);
	// if (callback != null)
	// callback.onList(list);
	// }
	// });
	// }

	public void retrieveBuilding(int buildingId, final StandardWebCallback<Element> callback) {
		((ElementWebDao) standardRestDao).retrieveBuilding(buildingId, new StandardWebCallback<Element>() {
			public void onResult(StandardWebResult<Element> result, boolean isUpdated) {
				standardDbDao.addAll(result.getList());
				boolean updated = modelAdapter.addModels(result.getList());
				if (callback != null)
					callback.onResult(result, updated);
			}
		});
	}

}
