package com.skarbo.campusguide.mapper.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

import com.google.gson.Gson;
import com.skarbo.campusguide.mapper.util.Coordinates;
import com.skarbo.campusguide.mapper.util.SQLiteHelper;

public class Element extends ModelAbstract {

	public static final String TAG = Element.class.getSimpleName();
	private int floorId;
	private String name;
	private String type;
	private String typeGroup;
	private List<Coordinates> coordinates;

	public Element() {
		coordinates = new ArrayList<Coordinates>();
	}

	public String getType() {
		return type;
	}

	public String getTypeGroup() {
		return typeGroup;
	}

	public int getForeignId() {
		return getFloorId();
	}

	public int getFloorId() {
		return floorId;
	}

	public String getName() {
		return name;
	}

	public List<Coordinates> getCoordinates() {
		return coordinates;
	}

	@Override
	public String toString() {
		return String.format("Id: %d, Floor id: %d, Name: %s, Coordinates: %s", this.getId(), this.getFloorId(),
				this.getName(), Arrays.toString(this.getCoordinates().toArray()));
	}

	public static class ElementFactory implements Factory<Element> {

		public Element generate(JSONObject jsonObject) {
			try {
				Gson gson = new Gson();
				return gson.fromJson(jsonObject.toString(), Element.class);
			} catch (Exception e) {
				Log.e(TAG, "ElementFactory.generate: " + e.getMessage() + "\n" + jsonObject.toString());
			}
			return null;
			// try {
			// Element element = new Element();
			// element.setId(jsonObject.getInt("id"));
			// element.setFloorId(jsonObject.getInt("floorId"));
			// element.setName(jsonObject.getString("name"));
			// if (!jsonObject.isNull("type"))
			// element.setType(jsonObject.getString("type"));
			// if (!jsonObject.isNull("typeGroup"))
			// element.setTypeGroup(jsonObject.getString("typeGroup"));
			// element.setCoordinates(Coordinate.initateCoordinateList(jsonObject.getJSONArray("coordinates")));
			//
			// int updated = !jsonObject.isNull("updated") ?
			// jsonObject.getInt("updated") : 0;
			// element.setUpdated(Math.max(updated,
			// jsonObject.getInt("registered")));
			//
			// return element;
			// } catch (JSONException e) {
			// Log.e(TAG, e.getMessage());
			// return null;
			// }
		}

		public Element generate(Cursor cursor) {
			Element element = new Element();

			element.id = cursor.getInt(SQLiteHelper.ElementSql.INDEX_COLUMN_ID);
			element.floorId = cursor.getInt(SQLiteHelper.ElementSql.INDEX_COLUMN_FLOOR_ID);
			element.name = cursor.getString(SQLiteHelper.ElementSql.INDEX_COLUMN_NAME);
			element.type = cursor.getString(SQLiteHelper.ElementSql.INDEX_COLUMN_TYPE);
			element.typeGroup = cursor.getString(SQLiteHelper.ElementSql.INDEX_COLUMN_TYPE_GROUP);

			String coordinatesJson = cursor.getString(SQLiteHelper.ElementSql.INDEX_COLUMN_COORDINATES);
			element.coordinates = Coordinates.initiate(coordinatesJson);

			return element;
		}
	}

}
