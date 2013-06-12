package com.skarbo.campusguide.mapper.model;

import java.io.Serializable;
import java.util.Arrays;

import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

import com.google.gson.Gson;
import com.skarbo.campusguide.mapper.util.SQLiteHelper;

public class Building extends ModelAbstract implements Serializable {
	private static final long serialVersionUID = 3652294773453186242L;

	private static final String TAG = Building.class.getSimpleName();

	private int facilityId;
	private String name;
	private String[] address;
	private Double[] location;
	private Double[] position;
	private String[] overlay;
	private int floors;

	public Building() {

	}

	public String[] getOverlay() {
		return overlay;
	}

	public void setOverlay(String[] overlay) {
		this.overlay = overlay;
	}

	public int getForeignId() {
		return getFacilityId();
	}

	public Building(String name) {
		this.name = name;
	}

	public int getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(int facilityId) {
		this.facilityId = facilityId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String[] getAddress() {
		return address;
	}

	public void setAddress(String[] address) {
		this.address = address;
	}

	public Double[] getLocation() {
		return location;
	}

	public void setLocation(Double[] location) {
		this.location = location;
	}

	public Double[] getPosition() {
		return position;
	}

	public void setPosition(Double[] position) {
		this.position = position;
	}

	public int getFloors() {
		return floors;
	}

	public void setFloors(int floorsCount) {
		this.floors = floorsCount;
	}

	@Override
	public String toString() {
		return String.format("Id: %d, Facility id: %d, Name: %s, Address: %s, Location: %s, Position: %s, Overlay: %s", this.id, this.facilityId, this.name,
				Arrays.toString(this.address), Arrays.toString(this.location), Arrays.toString(this.position), Arrays.toString(this.overlay));
	}

	public static class BuildingFactory implements Factory<Building> {

		public Building generate(JSONObject jsonObject) {
			try {
				Gson gson = new Gson();
				return gson.fromJson(jsonObject.toString(), Building.class);
			} catch (Exception e) {
				Log.e(TAG, "BuildingFactory.generate: " + e.getMessage() + "\n" + jsonObject.toString());
			}
			return null;
			//
			// try {
			// Building building = new Building();
			// building.setId(jsonObject.getInt("id"));
			// building.setName(jsonObject.getString("name"));
			// building.setFacilityId(jsonObject.getInt("facilityId"));
			// building.setFloorsCount(jsonObject.getInt("floors"));
			// if (!jsonObject.isNull("overlay") &&
			// jsonObject.getString("overlay") != "")
			// building.setOverlay(jsonObject.getString("overlay").split(","));
			//
			// int updated = !jsonObject.isNull("updated") ?
			// jsonObject.getInt("updated") : 0;
			// building.setUpdated(Math.max(updated,
			// jsonObject.getInt("registered")));
			//
			// // Address
			// JSONArray addressJson = jsonObject.getJSONArray("address");
			// String[] address = new String[addressJson.length()];
			// for (int i = 0; i < addressJson.length(); i++)
			// address[i] = addressJson.getString(i);
			// building.setAddress(address);
			//
			// // Location
			// JSONArray locationJson = jsonObject.getJSONArray("location");
			// Double[] location = new Double[locationJson.length()];
			// for (int i = 0; i < locationJson.length(); i++)
			// location[i] = locationJson.getDouble(i);
			// building.setLocation(location);
			//
			// // Position
			// JSONArray positionsJson = jsonObject.getJSONArray("position");
			// Double[] position = new Double[positionsJson.length()];
			// for (int i = 0; i < positionsJson.length(); i++) {
			// position[i] = positionsJson.getDouble(i);
			// }
			// building.setPosition(position);
			//
			// return building;
			// } catch (JSONException e) {
			// Log.e(TAG, e.getMessage());
			// return null;
			// }
		}

		public Building generate(Cursor cursor) {
			Building building = new Building();
			if (cursor == null)
				return null;

			Gson gson = new Gson();

			building.id = cursor.getInt(SQLiteHelper.BuildingSql.INDEX_COLUMN_ID);
			building.facilityId = cursor.getInt(SQLiteHelper.BuildingSql.INDEX_COLUMN_FACILITY_ID);
			building.name = cursor.getString(SQLiteHelper.BuildingSql.INDEX_COLUMN_NAME);
			building.floors = cursor.getInt(SQLiteHelper.BuildingSql.INDEX_COLUMN_FLOOR_COUNT);
			building.updated = cursor.getInt(SQLiteHelper.BuildingSql.INDEX_COLUMN_UPDATED);

			try {
				String address = cursor.getString(SQLiteHelper.BuildingSql.INDEX_COLUMN_ADDRESS);
				if (address != "")
					building.address = gson.fromJson(address, String[].class);
			} catch (Exception e) {
				Log.e(TAG, "BuildingFactory.generate: " + e.getMessage());
			}

			try {
				String overlay = cursor.getString(SQLiteHelper.BuildingSql.INDEX_COLUMN_OVERLAY);
				if (overlay != "")
					building.overlay = gson.fromJson(overlay, String[].class);
			} catch (Exception e) {
				Log.e(TAG, "BuildingFactory.generate: " + e.getMessage());
			}

			try {
				String locationString = cursor.getString(SQLiteHelper.BuildingSql.INDEX_COLUMN_LOCATION);
				if (locationString != "")
					building.location = gson.fromJson(locationString, Double[].class);
			} catch (Exception e) {
				Log.e(TAG, "BuildingFactory.generate: " + e.getMessage());
			}

			try {
				String positionString = cursor.getString(SQLiteHelper.BuildingSql.INDEX_COLUMN_POSITION);
				if (positionString != "")
					building.position = gson.fromJson(positionString, Double[].class);
			} catch (Exception e) {
				Log.e(TAG, "BuildingFactory.generate: " + e.getMessage());
			}

			return building;
		}
	}

}
