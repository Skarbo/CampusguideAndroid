package com.skarbo.campusguide.mapper.dao.web;

import com.skarbo.campusguide.mapper.model.Model;
import com.skarbo.campusguide.mapper.model.adapter.ModelAdapter;

public interface StandardWebDao<E extends Model> {

	public interface StandardWebCallback<T extends Model> {
		public void onResult(StandardWebResult<T> result, boolean isUpdated);
	}

	public static class StandardWebResult<E extends Model> {
		private E single;
		private ModelAdapter<E> list;

		public StandardWebResult(E single, ModelAdapter<E> list) {
			this.single = single;
			this.list = list;
		}

		public E getSingle() {
			return single;
		}

		public ModelAdapter<E> getList() {
			return list;
		}

	}

	public void retrieveAll(StandardWebCallback<E> callback);

	public void retrieve(int id, StandardWebCallback<E> callback);

	public void retrieveForeign(Integer[] foreignIds, StandardWebCallback<E> callback);

}
