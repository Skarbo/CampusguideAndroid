package com.skarbo.campusguide.mapper.dao;

import com.skarbo.campusguide.mapper.model.Element;
import com.skarbo.campusguide.mapper.model.adapter.ModelAdapter;

public interface ElementDao extends StandardDao<Element> {

	public ModelAdapter<Element> getBuilding(int buildingId);

}
