package com.skarbo.campusguide.mapper.util;

import java.util.Arrays;

import android.graphics.PointF;

@Deprecated
public class Coordinate extends PointF {

	private static final String TAG = Coordinate.class.getSimpleName();
	private static final String TYPE_LINE = "L";
	private static final String TYPE_QUAD = "Q";
	private static final String TYPE_BEIZER = "B";

	// private static final String COORDINATES_LIST_SPLITTER = "$";
	// private static final String COORDINATES_SPLITTER = "|";
	// private static final String COORDINATE_SPLITTER = ",";
	// private static final String COORDINATE_CONTROLS_SPLITTER = "%";

	public enum TYPE {
		LINE, QUAD, BEIZER
	}

	private TYPE type;
	private PointF[] controls;

	public TYPE getType() {
		return type;
	}

	public PointF[] getControls() {
		return controls;
	}

	@Override
	public String toString() {
		return String.format("[%s,%s,%s]", super.toString(), this.getType(), Arrays.toString(this.getControls()));
	}

	// public static String typeToString(TYPE type) {
	// if (type == null)
	// return "";
	// switch (type) {
	// case LINE:
	// return TYPE_LINE;
	// case BEIZER:
	// return TYPE_BEIZER;
	// case QUAD:
	// return TYPE_QUAD;
	// }
	// return "";
	// }

	// public static String toDataString(Coordinate coordinate) {
	// if (coordinate == null)
	// return "";
	// String controls = "";
	// if (coordinate.getControls() != null && coordinate.getControls().length >
	// 0) {
	// controls = Util.implode(coordinate.getControls(),
	// COORDINATE_CONTROLS_SPLITTER,
	// new Util.ImplodeCallback<PointF>() {
	// public String createString(PointF object) {
	// if (object != null)
	// return String.format("%s%s%s", String.valueOf(object.x),
	// COORDINATE_CONTROLS_SPLITTER,
	// String.valueOf(object.y));
	// else
	// return "";
	// }
	// });
	// }
	// return Util.implode(new String[] { String.valueOf(coordinate.x),
	// String.valueOf(coordinate.y),
	// typeToString(coordinate.getType()), controls }, COORDINATE_SPLITTER);
	// }
	//
	// public static String toDataString(Coordinate[] coordinates) {
	// return Util.implode(coordinates, COORDINATES_SPLITTER, new
	// Util.ImplodeCallback<Coordinate>() {
	// public String createString(Coordinate object) {
	// return toDataString(object);
	// }
	// });
	// }
	//
	// public static String toDataString(List<Coordinate[]> coordinates) {
	// return Util.implode(coordinates.toArray(), COORDINATES_LIST_SPLITTER, new
	// Util.ImplodeCallback<Coordinate[]>() {
	// public String createString(Coordinate[] object) {
	// return toDataString(object);
	// }
	// });
	// }
	//
	// private static Coordinate initateCoordinate(JSONArray jsonArray) {
	// if (jsonArray.length() >= 4) {
	// try {
	// Coordinate coordinate = new Coordinate();
	//
	// coordinate.set((float) jsonArray.getDouble(0), (float)
	// jsonArray.getDouble(1));
	//
	// String type = jsonArray.getString(2);
	// if (type.equalsIgnoreCase(TYPE_LINE)) {
	// coordinate.setType(TYPE.LINE);
	// } else if (type.equalsIgnoreCase(TYPE_QUAD)) {
	// coordinate.setType(TYPE.QUAD);
	// } else if (type.equalsIgnoreCase(TYPE_BEIZER)) {
	// coordinate.setType(TYPE.BEIZER);
	// }
	//
	// if (!jsonArray.isNull(3)) {
	// JSONArray controlArray = jsonArray.getJSONArray(3);
	// PointF[] controls = new PointF[controlArray.length() / 2];
	// for (int i = 0; i < controlArray.length(); i += 2)
	// controls[i] = new PointF((float) controlArray.getDouble(i),
	// (float) controlArray.getDouble(i + 1));
	// coordinate.setControls(controls);
	// }
	//
	// return coordinate;
	// } catch (JSONException e) {
	// Log.e(TAG, e.getMessage() + " - " + jsonArray.toString());
	// }
	// }
	// return null;
	// }
	//
	// private static Coordinate initateCoordinate(String coordinatesString) {
	// String coordinates[] = coordinatesString.split("\\" +
	// COORDINATE_SPLITTER);
	// if (coordinates.length >= 3) {
	// Coordinate coordinate = new Coordinate();
	//
	// coordinate.set(Float.parseFloat(coordinates[0]),
	// Float.parseFloat(coordinates[1]));
	//
	// String type = coordinates[2];
	// if (type.equalsIgnoreCase(TYPE_LINE)) {
	// coordinate.setType(TYPE.LINE);
	// } else if (type.equalsIgnoreCase(TYPE_QUAD)) {
	// coordinate.setType(TYPE.QUAD);
	// } else if (type.equalsIgnoreCase(TYPE_BEIZER)) {
	// coordinate.setType(TYPE.BEIZER);
	// }
	//
	// if (coordinates.length >= 4) {
	// String[] controlsArray =
	// coordinates[3].split(COORDINATE_CONTROLS_SPLITTER);
	// if (coordinates[3] != "") {
	// PointF[] controls = new PointF[controlsArray.length / 2];
	// for (int i = 0; i < controlsArray.length && controlsArray.length % 2 ==
	// 0; i += 2) {
	// try {
	// if (controlsArray[i] != "" && controlsArray[i + 1] != "")
	// controls[i] = new PointF(Float.parseFloat(controlsArray[i]),
	// Float.parseFloat(controlsArray[i + 1]));
	// } catch (Exception e) {
	// }
	// }
	// coordinate.setControls(controls);
	// }
	// }
	//
	// return coordinate;
	// }
	// return null;
	// }
	//
	// private static Coordinate[] initateCoordinates(JSONArray jsonArray) {
	// Coordinate[] coordinates = new Coordinate[jsonArray.length()];
	// for (int i = 0; i < jsonArray.length(); i++) {
	// try {
	// coordinates[i] = initateCoordinate(jsonArray.getJSONArray(i));
	// } catch (JSONException e) {
	// Log.e(TAG, e.getMessage() + " - " + jsonArray.toString());
	// }
	// }
	// return coordinates;
	// }
	//
	// private static Coordinate[] initateCoordinates(String coordinatesString)
	// {
	// String[] coordinates = coordinatesString.split("\\" +
	// COORDINATES_SPLITTER);
	// Coordinate[] coordinatesArray = new Coordinate[coordinates.length];
	// if (coordinatesString != "") {
	// for (int i = 0; i < coordinatesArray.length; i++) {
	// coordinatesArray[i] = initateCoordinate(coordinates[i]);
	// }
	// }
	// return coordinatesArray;
	// }
	//
	// public static List<Coordinate[]> initateCoordinateList(JSONArray
	// jsonArray) {
	// List<Coordinate[]> coordinates = new ArrayList<Coordinate[]>();
	// for (int i = 0; i < jsonArray.length(); i++) {
	// try {
	// coordinates.add(initateCoordinates(jsonArray.getJSONArray(i)));
	// } catch (JSONException e) {
	// Log.e(TAG, e.getMessage() + " - " + jsonArray.toString());
	// }
	// }
	// return coordinates;
	// }
	//
	// public static List<Coordinate[]> initateCoordinateList(String
	// coordinatesString) {
	// List<Coordinate[]> coordinatesList = new ArrayList<Coordinate[]>();
	// String[] coordinates = coordinatesString.split("\\" +
	// COORDINATES_LIST_SPLITTER);
	// if (coordinatesString != "") {
	// for (int i = 0; i < coordinates.length; i++) {
	// coordinatesList.add(initateCoordinates(coordinates[i]));
	// }
	// }
	// return coordinatesList;
	// }

	public static Coordinate initiate(String[] coordinateArray) {
		Coordinate coordinate = new Coordinate();

		if (coordinateArray.length >= 2) {
			coordinate.x = Float.valueOf(coordinateArray[0]);
			coordinate.y = Float.valueOf(coordinateArray[1]);
		}
		if (coordinateArray.length >= 3) {
			String type = coordinateArray[2];
			if (type.equalsIgnoreCase(TYPE_LINE)) {
				coordinate.type = TYPE.LINE;
			} else if (type.equalsIgnoreCase(TYPE_QUAD)) {
				coordinate.type = TYPE.QUAD;
			} else if (type.equalsIgnoreCase(TYPE_BEIZER)) {
				coordinate.type = TYPE.BEIZER;
			}
		}
		if (coordinateArray.length >= 7) {
			coordinate.controls = new PointF[2];
			coordinate.controls[0] = new PointF(Float.valueOf(coordinateArray[3]), Float.valueOf(coordinateArray[4]));
			coordinate.controls[1] = new PointF(Float.valueOf(coordinateArray[5]), Float.valueOf(coordinateArray[6]));
		} else if (coordinateArray.length >= 5) {
			coordinate.controls = new PointF[1];
			coordinate.controls[0] = new PointF(Float.valueOf(coordinateArray[3]), Float.valueOf(coordinateArray[4]));
		}

		return coordinate;
	}
}
