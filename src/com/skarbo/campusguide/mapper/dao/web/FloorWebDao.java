package com.skarbo.campusguide.mapper.dao.web;

import com.skarbo.campusguide.mapper.model.Floor;

public interface FloorWebDao extends StandardWebDao<Floor> {


	public void retrieveMain(Integer[] buildingIds, StandardWebCallback<Floor> callback);
	
}
