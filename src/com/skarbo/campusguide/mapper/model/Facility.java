package com.skarbo.campusguide.mapper.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.skarbo.campusguide.mapper.util.SQLiteHelper;

import android.database.Cursor;
import android.util.Log;

public class Facility extends ModelAbstract {

	public static final String TAG = Facility.class.getSimpleName();
	private String name = "";
	private List<Building> buildingsList;
	private int buildings = 0;

	public Facility() {
		this.buildingsList = new ArrayList<Building>();
	}

	public int getForeignId() {
		return 0;
	}

	public List<Building> getBuildings() {
		return buildingsList;
	}

	public int getBuildingsCount() {
		return buildings;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "Id: " + this.id + ", Name: " + this.name + ", Buildings: " + this.buildings;
	}

	public static class FacilityFactory implements Factory<Facility> {

		public Facility generate(JSONObject jsonObject) {
			try {
				Gson gson = new Gson();
				return gson.fromJson(jsonObject.toString(), Facility.class);				
			} catch (Exception e) {
				Log.e(TAG, "FacilityFactory.generate: " + e.getMessage() + "\n" + jsonObject.toString());
			}
			return null;
			// try {
			//
			// Facility facility = new Facility();
			// facility.setId(jsonObject.getInt("id"));
			// facility.setName(jsonObject.getString("name"));
			// facility.setBuildingsCount(jsonObject.getInt("buildings"));
			//
			// int updated = !jsonObject.isNull("updated") ?
			// jsonObject.getInt("updated") : 0;
			// facility.setUpdated(Math.max(updated,
			// jsonObject.getInt("registered")));
			//
			// return facility;
			// } catch (JSONException e) {
			// Log.e(TAG, e.getMessage());
			// return null;
			// }
		}

		public Facility generate(Cursor cursor) {
			Facility facility = new Facility();

			facility.id = cursor.getInt(SQLiteHelper.FacilitySql.INDEX_COLUMN_ID);
			facility.name = cursor.getString(SQLiteHelper.FacilitySql.INDEX_COLUMN_NAME);
			facility.buildings = cursor.getInt(SQLiteHelper.FacilitySql.INDEX_COLUMN_BUILDING_COUNT);
			facility.updated = cursor.getInt(SQLiteHelper.FacilitySql.INDEX_COLUMN_UPDATED);

			return facility;
		}

	}

}
