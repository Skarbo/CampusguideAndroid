package com.skarbo.campusguide.mapper.model;

import java.util.List;

import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

import com.google.gson.Gson;
import com.skarbo.campusguide.mapper.util.Coordinates;
import com.skarbo.campusguide.mapper.util.SQLiteHelper;

public class Floor extends ModelAbstract {

	public static final String TAG = Floor.class.getSimpleName();

	private int buildingId;
	private String name;
	private List<Coordinates> coordinates;
	private int order;
	private int main;

	public int getForeignId() {
		return getBuildingId();
	}

	public int getBuildingId() {
		return buildingId;
	}

	public String getName() {
		return name;
	}

	public List<Coordinates> getCoordinates() {
		return coordinates;
	}

	public int getOrder() {
		return order;
	}

	public boolean isMain() {
		return main > 0;
	}

	//
	// @Override
	// public String toString() {
	// return
	// String.format("Id: %d, Building id: %d, Name: %s, Order: %d, Main: %s, Coordinates: %s",
	// this.getId(),
	// this.getBuildingId(), this.getName(), this.getOrder(), this.isMain(),
	// Coordinate.toDataString(this.getCoordinates()));
	// }

	public static class FloorFactory implements Factory<Floor> {

		public Floor generate(JSONObject jsonObject) {
			try {
				Gson gson = new Gson();
				return gson.fromJson(jsonObject.toString(), Floor.class);
			} catch (Exception e) {
				Log.e(TAG, "FloorFactory.generate: " + e.getMessage() + "\n" + jsonObject.toString());
			}
			return null;
			// try {
			// Floor floor = new Floor();
			// floor.setId(jsonObject.getInt("id"));
			// floor.buildingId = jsonObject.getInt("buildingId");
			// floor.name = jsonObject.getString("name");
			// floor.order = jsonObject.getInt("order");
			// floor.main = jsonObject.getInt("main") > 0;
			// floor.setCoordinates(Coordinate.initateCoordinateList(jsonObject.getJSONArray("coordinates")));
			//
			// int updated = !jsonObject.isNull("updated") ?
			// jsonObject.getInt("updated") : 0;
			// floor.setUpdated(Math.max(updated,
			// jsonObject.getInt("registered")));
			//
			// return floor;
			// } catch (JSONException e) {
			// Log.e(TAG, e.getMessage());
			// return null;
			// }
		}

		public Floor generate(Cursor cursor) {
			Floor floor = new Floor();

			floor.id = cursor.getInt(SQLiteHelper.FloorSql.INDEX_COLUMN_ID);
			floor.buildingId = cursor.getInt(SQLiteHelper.FloorSql.INDEX_COLUMN_BUILDING_ID);
			floor.name = cursor.getString(SQLiteHelper.FloorSql.INDEX_COLUMN_NAME);
			floor.order = cursor.getInt(SQLiteHelper.FloorSql.INDEX_COLUMN_ORDER);
			floor.updated = cursor.getInt(SQLiteHelper.FloorSql.INDEX_COLUMN_UPDATED);
			floor.main = cursor.getInt(SQLiteHelper.FloorSql.INDEX_COLUMN_MAIN);

			String coordinatesJson = cursor.getString(SQLiteHelper.FloorSql.INDEX_COLUMN_COORDINATES);
			floor.coordinates = Coordinates.initiate(coordinatesJson);

			return floor;
		}
	}

}
