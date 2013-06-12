package com.skarbo.campusguide.mapper.container;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.google.gson.Gson;
import com.skarbo.campusguide.mapper.model.Building;
import com.skarbo.campusguide.mapper.model.Element;
import com.skarbo.campusguide.mapper.model.Floor;
import com.skarbo.campusguide.mapper.model.Navigation;

public class BuildingContainer {

	private static final String TAG = BuildingContainer.class.getSimpleName();
	
	public Building building;
	public List<Element> elements = new ArrayList<Element>();
	public List<Floor> floors = new ArrayList<Floor>();
	public List<Navigation> navigations = new ArrayList<Navigation>();
	
	public static BuildingContainer generate(String json)
	{
		try {
			Gson gson = new Gson();
			return gson.fromJson(json, BuildingContainer.class);
		} catch (Exception e) {
			Log.e(TAG, "BuildingContainer.generate: " + e.getMessage());
		}
		return null;
	}

}
