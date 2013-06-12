package com.skarbo.campusguide.mapper.dao;

import com.skarbo.campusguide.mapper.model.Model;
import com.skarbo.campusguide.mapper.model.adapter.ModelAdapter;

public interface StandardDao<E extends Model> {

	public ModelAdapter<E> getAll();

	public E get(final int id);

	public ModelAdapter<E> getForeign(final Integer[] foreignIds);

}
