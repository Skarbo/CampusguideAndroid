package com.skarbo.campusguide.mapper.model;

import java.io.Serializable;

import org.json.JSONObject;

import com.google.gson.Gson;

import android.database.Cursor;
import android.graphics.PointF;
import android.util.Log;

public class Navigation extends ModelAbstract implements Serializable {

	private static final String TAG = Building.class.getSimpleName();
	private static final long serialVersionUID = 7780233660357900097L;

	private int floorId;
	private int elementId;
	private PointF coordinate;
	private int updated;

	public int getForeignId() {
		return getFloorId();
	}

	public int getFloorId() {
		return floorId;
	}

	public void setFloorId(int floorId) {
		this.floorId = floorId;
	}

	public int getElementId() {
		return elementId;
	}

	public void setElementId(int elementId) {
		this.elementId = elementId;
	}

	public PointF getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(PointF coordinate) {
		this.coordinate = coordinate;
	}

	public int getUpdated() {
		return updated;
	}

	public void setUpdated(int updated) {
		this.updated = updated;
	}
	
	public static class NavigationFactory implements Factory<Navigation> {

		public Navigation generate(JSONObject jsonObject) {
			try {
				Gson gson = new Gson();
				return gson.fromJson(jsonObject.toString(), Navigation.class);
			} catch (Exception e) {
				Log.e(TAG, "BuildingFactory.generate: " + e.getMessage() + "\n" + jsonObject.toString());
			}
			return null;
		}

		public Navigation generate(Cursor cursor) {
			if (cursor == null)
				return null;
			return null;
		}
		
	}

}
