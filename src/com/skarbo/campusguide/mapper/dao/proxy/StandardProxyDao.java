package com.skarbo.campusguide.mapper.dao.proxy;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import com.skarbo.campusguide.mapper.MainActivity;
import com.skarbo.campusguide.mapper.dao.StandardDao;
import com.skarbo.campusguide.mapper.dao.db.StandardDbDao;
import com.skarbo.campusguide.mapper.dao.web.StandardWebDao;
import com.skarbo.campusguide.mapper.dao.web.rest.StandardRestWebDao;
import com.skarbo.campusguide.mapper.model.Factory;
import com.skarbo.campusguide.mapper.model.Model;
import com.skarbo.campusguide.mapper.model.adapter.ModelAdapter;

public abstract class StandardProxyDao<E extends Model> implements StandardDao<E>, StandardWebDao<E> {

	private static final String TAG = StandardProxyDao.class.getSimpleName();

	protected Context context;
	protected int mode;
	protected SQLiteDatabase database;
	protected ModelAdapter<E> modelAdapter;
	protected StandardRestWebDao<E> standardRestDao;
	protected StandardDbDao<E> standardDbDao;


	public StandardProxyDao(Context context, SQLiteDatabase database, int mode) {
		this.context = context;
		this.mode = mode;
		this.database = database;
		this.modelAdapter = new ModelAdapter<E>();
		this.standardRestDao = new StandardRestWebDao<E>(context, getController(), mode, getFactory());
		this.standardDbDao = getStandardDbDao();		
	}

	protected abstract Factory<E> getFactory();

	protected abstract String getController();

	public abstract StandardDbDao<E> getStandardDbDao();

	public ModelAdapter<E> getList() {
		return modelAdapter;
	}

	public ModelAdapter<E> getAll() {
		ModelAdapter<E> list = this.standardDbDao.getAll();
		modelAdapter.addModels(list);
		return modelAdapter;
	}

	public void retrieveAll(final StandardWebCallback<E> callback) {
		standardRestDao.retrieveAll(new StandardWebCallback<E>() {
			public void onResult(StandardWebResult<E> result, boolean isUpdated) {
				standardDbDao.addAll(result.getList());
				boolean updated = modelAdapter.addModels(result.getList());
				if (callback != null)
					callback.onResult(result, updated);
			}
		});
	}

	public E get(int id) {
		E single = this.standardDbDao.get(id);
		modelAdapter.addModel(single);
		return single;
	}

	public void retrieve(int id, final StandardWebCallback<E> callback) {
		standardRestDao.retrieve(id, new StandardWebCallback<E>() {
			public void onResult(StandardWebResult<E> result, boolean isUpdated) {
				boolean updated = false;
				if (result.getSingle() != null) {
					standardDbDao.add(result.getSingle());
					updated = modelAdapter.addModel(result.getSingle());
				}
				if (callback != null)
					callback.onResult(result, updated);
			}
		});
	}

	public ModelAdapter<E> getForeign(Integer foreignId) {
		return getForeign(new Integer[] { foreignId });
	}

	public ModelAdapter<E> getForeign(Integer[] foreignIds) {
		ModelAdapter<E> list = this.standardDbDao.getForeign(foreignIds);
		modelAdapter.addModels(list);
		return list;
	}

	public void retrieveForeign(Integer[] foreignIds, final StandardWebCallback<E> callback) {
		standardRestDao.retrieveForeign(foreignIds, new StandardWebCallback<E>() {
			public void onResult(StandardWebResult<E> result, boolean isUpdated) {
				standardDbDao.addAll(result.getList());
				boolean updated = modelAdapter.addModels(result.getList());
				if (callback != null)
					callback.onResult(result, updated);
			}
		});
	}

}
