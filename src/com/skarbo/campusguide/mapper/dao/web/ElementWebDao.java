package com.skarbo.campusguide.mapper.dao.web;

import com.skarbo.campusguide.mapper.model.Element;

public interface ElementWebDao extends StandardWebDao<Element> {

	public void retrieveBuilding(int buildingId, StandardWebCallback<Element> callback);

}
