package com.skarbo.campusguide.mapper.model.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.database.DataSetObserver;

import com.skarbo.campusguide.mapper.model.Model;

public class ModelAdapter<E extends Model> extends ArrayList<E> {
	private static final long serialVersionUID = -8940644405090971789L;

	private static final String TAG = ModelAdapter.class.getSimpleName();

	public interface FilterCallback<E> {
		public boolean isFilter(E model);
	}

	private List<DataSetObserver> dataSetObservers;

	public ModelAdapter() {
		dataSetObservers = new ArrayList<DataSetObserver>();
	}

	public boolean isInList(int id) {
		for (E model : this) {
			if (model.getId() == id) {
				return true;
			}
		}
		return false;
	}

	public Integer[] getIds() {
		Integer[] ids = new Integer[size()];
		for (int i = 0; i < size(); i++) {
			if (get(i) != null)
				ids[i] = get(i).getId();
		}
		return ids;
	}

	public Integer[] getForeignIds() {
		List<Integer> foreignIds = new ArrayList<Integer>();
		for (E model : this) {
			if (!foreignIds.contains(model.getForeignId())) {
				foreignIds.add(model.getForeignId());
			}
		}
		return (Integer[]) foreignIds.toArray();
	}

	public boolean addModel(E model) {
		int index = indexOf(model);
		boolean updated = false;
		if (index > -1) {
			set(index, model);
			if (model.isUpdated(get(index)))
				updated = true;
		} else {
			add(model);
			updated = true;
		}
		if (updated)
			doNotifyObservers();
		return updated;
	}

	public boolean addModels(List<E> models) {
		boolean updated = false;
		for (E model : models) {
			int index = indexOf(model);
			if (model == null)
				continue;
			if (index > -1) {
				set(index, model);
				if (model.isUpdated(get(index)))
					updated = true;
			} else {
				add(model);
				updated = true;
			}
		}
		if (updated)
			doNotifyObservers();
		return updated;
	}

	public E getId(int id) {
		for (E model : this) {
			if (model.getId() == id) {
				return model;
			}
		}
		return null;
	}

	public ModelAdapter<E> getForeign(Integer foreignId) {
		return getForeign(new Integer[] { foreignId });
	}

	public ModelAdapter<E> getForeign(Integer[] foreignIds) {
		ModelAdapter<E> list = new ModelAdapter<E>();
		List<Integer> foreignIdsList = Arrays.asList(foreignIds);
		for (E model : this) {
			if (foreignIdsList.contains(model.getForeignId())) {
				list.add(model);
			}
		}
		return list;
	}

	public ModelAdapter<E> getFilter(FilterCallback<E> filter) {
		ModelAdapter<E> list = new ModelAdapter<E>();
		for (E model : this) {
			if (filter.isFilter(model)) {
				list.add(model);
			}
		}
		return list;
	}

	@Override
	public String toString() {
		return Arrays.toString(this.toArray());
	}

	public void registerDataSetObserver(DataSetObserver observer) {
		dataSetObservers.add(observer);
	}

	public void unregisterDataSetObserver(DataSetObserver observer) {
		dataSetObservers.remove(observer);
	}

	protected void doNotifyObservers() {
		doNotifyObservers(false);
	}

	/**
	 * @param isInvalidated
	 *            True if invalidated, else changed
	 */
	protected void doNotifyObservers(boolean isInvalidated) {
		for (DataSetObserver dataSetObserver : dataSetObservers) {
			if (dataSetObserver != null) {
				if (isInvalidated)
					dataSetObserver.onInvalidated();
				else
					dataSetObserver.onChanged();
			}
		}
	}

}
