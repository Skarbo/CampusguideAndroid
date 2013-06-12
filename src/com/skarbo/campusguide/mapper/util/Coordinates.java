package com.skarbo.campusguide.mapper.util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;

import android.graphics.PointF;
import android.util.Log;

public class Coordinates {

	private static final String TAG = Coordinates.class.getSimpleName();

	public List<String[]> coordinates;
	public String[] center;
	@Expose
	public List<PointF> coordinatesList;
	@Expose
	public PointF centerPoint;

	public Coordinates() {
		coordinates = new ArrayList<String[]>();
	}

	public List<PointF> getCoordinates() {
		if (coordinatesList == null) {
			coordinatesList = new ArrayList<PointF>();
			for (String[] coordinate : coordinates) {
				if (coordinate.length >= 2)
					coordinatesList.add(new PointF(Float.parseFloat(coordinate[0]), Float.parseFloat(coordinate[1])));
			}
		}
		return coordinatesList;
	}

	public String[] getCenter() {
		return center;
	}

	public PointF getCenterPoint() {
		if (this.center != null && this.center.length >= 2 && this.centerPoint == null) {
			this.centerPoint = new PointF(Float.valueOf(center[0]), Float.valueOf(center[1]));
		}
		return this.centerPoint;
	}

	public static List<Coordinates> initiate(String json) {
		try {
			Gson gson = new Gson();
			Type collectionType = new TypeToken<List<Coordinates>>() {
			}.getType();
			return gson.fromJson(json, collectionType);
		} catch (Exception e) {
			Log.e(TAG, "ElementFactory.generate: " + e.getMessage() + "\n" + json);
		}
		return new ArrayList<Coordinates>();
	}

}
