package com.skarbo.campusguide.mapper.dao;

import com.skarbo.campusguide.mapper.model.Floor;
import com.skarbo.campusguide.mapper.model.adapter.ModelAdapter;

public interface FloorDao extends StandardDao<Floor> {

	public ModelAdapter<Floor> getMain(Integer[] buildingIds);

}
