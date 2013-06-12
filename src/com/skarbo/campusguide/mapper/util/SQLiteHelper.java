package com.skarbo.campusguide.mapper.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper {

	public static final String COLUMN_ID_DEFAULT = "id";

	public static final class FacilitySql {
		public static final String TABLE = "FACILITY";
		public static final String COLUMN_ID = "id";
		public static final String COLUMN_NAME = "name";
		public static final String COLUMN_BUILDING_COUNT = "building_count";
		public static final String COLUMN_UPDATED = "updated";

		public static final int INDEX_COLUMN_ID = 0;
		public static final int INDEX_COLUMN_NAME = 1;
		public static final int INDEX_COLUMN_BUILDING_COUNT = 2;
		public static final int INDEX_COLUMN_UPDATED = 3;

		private static final String DATABASE_CREATE = String.format("CREATE TABLE %s (%s MEDIUMINT PRIMARY KEY, "
				+ "%s VARCHAR(255) NOT NULL, %s SMALLINT, %s INT)", FacilitySql.TABLE, FacilitySql.COLUMN_ID,
				FacilitySql.COLUMN_NAME, FacilitySql.COLUMN_BUILDING_COUNT, COLUMN_UPDATED);
	}

	public static final class BuildingSql {
		public static final String TABLE = "BUILDING";
		public static final String COLUMN_ID = "id";
		public static final String COLUMN_FACILITY_ID = "facility_id";
		public static final String COLUMN_NAME = "name";
		public static final String COLUMN_ADDRESS = "address";
		public static final String COLUMN_LOCATION = "location";
		public static final String COLUMN_POSITION = "position";
		public static final String COLUMN_OVERLAY = "overlay";
		public static final String COLUMN_FLOOR_COUNT = "floor_count";
		public static final String COLUMN_UPDATED = "updated";

		public static final int INDEX_COLUMN_ID = 0;
		public static final int INDEX_COLUMN_FACILITY_ID = 1;
		public static final int INDEX_COLUMN_NAME = 2;
		public static final int INDEX_COLUMN_ADDRESS = 3;
		public static final int INDEX_COLUMN_LOCATION = 4;
		public static final int INDEX_COLUMN_POSITION = 5;
		public static final int INDEX_COLUMN_OVERLAY = 6;
		public static final int INDEX_COLUMN_FLOOR_COUNT = 7;
		public static final int INDEX_COLUMN_UPDATED = 8;

		private static final String DATABASE_CREATE = String.format(
				"CREATE TABLE %s (%s MEDIUMINT PRIMARY KEY, %s MEDIUMINT, "
						+ "%s VARCHAR(255) NOT NULL, %s VARCHAR(255) NOT NULL,"
						+ " %s VARCHAR(255) NOT NULL, %s VARCHAR(255) NOT NULL, %s TEXT, %s SMALLINT, %s INT)", TABLE,
				COLUMN_ID, COLUMN_FACILITY_ID, COLUMN_NAME, COLUMN_ADDRESS, COLUMN_LOCATION, COLUMN_POSITION,
				COLUMN_OVERLAY, COLUMN_FLOOR_COUNT, COLUMN_UPDATED);
	}

	public static final class FloorSql {
		public static final String TABLE = "FLOOR";
		public static final String COLUMN_ID = "id";
		public static final String COLUMN_BUILDING_ID = "building_id";
		public static final String COLUMN_NAME = "name";
		public static final String COLUMN_COORDINATES = "coordinates";
		public static final String COLUMN_ORDER = "floor_order";
		public static final String COLUMN_MAIN = "main";
		public static final String COLUMN_UPDATED = "updated";

		public static final int INDEX_COLUMN_ID = 0;
		public static final int INDEX_COLUMN_BUILDING_ID = 1;
		public static final int INDEX_COLUMN_NAME = 2;
		public static final int INDEX_COLUMN_COORDINATES = 3;
		public static final int INDEX_COLUMN_ORDER = 4;
		public static final int INDEX_COLUMN_MAIN = 5;
		public static final int INDEX_COLUMN_UPDATED = 6;

		private static final String DATABASE_CREATE = String.format("CREATE TABLE %s (%s MEDIUMINT PRIMARY KEY, "
				+ "%s MEDIUMINT, %s VARCHAR(255) NOT NULL, %s TEXT, '%s' SMALLINT, '%s' TINYINT, %s INT)", TABLE,
				COLUMN_ID, COLUMN_BUILDING_ID, COLUMN_NAME, COLUMN_COORDINATES, COLUMN_ORDER, COLUMN_MAIN,
				COLUMN_UPDATED);
	}

	public static final class ElementSql {
		public static final String TABLE = "ELEMENT";
		public static final String COLUMN_ID = "id";
		public static final String COLUMN_FLOOR_ID = "floor_id";
		public static final String COLUMN_NAME = "name";
		public static final String COLUMN_COORDINATES = "coordinates";
		public static final String COLUMN_TYPE = "type";
		public static final String COLUMN_TYPE_GROUP = "room";
		public static final String COLUMN_UPDATED = "updated";

		public static final int INDEX_COLUMN_ID = 0;
		public static final int INDEX_COLUMN_FLOOR_ID = 1;
		public static final int INDEX_COLUMN_NAME = 2;
		public static final int INDEX_COLUMN_COORDINATES = 3;
		public static final int INDEX_COLUMN_TYPE = 4;
		public static final int INDEX_COLUMN_TYPE_GROUP = 5;
		public static final int INDEX_COLUMN_UPDATED = 6;

		private static final String DATABASE_CREATE = String.format("CREATE TABLE %s (%s MEDIUMINT PRIMARY KEY, "
				+ "%s MEDIUMINT, %s VARCHAR(255) NOT NULL, %s VARCHAR(255), %s VARCHAR(255), %s TINYINT, %s INT)",
				TABLE, COLUMN_ID, COLUMN_FLOOR_ID, COLUMN_NAME, COLUMN_COORDINATES, COLUMN_TYPE, COLUMN_TYPE_GROUP,
				COLUMN_UPDATED);
	}

	public static final class NavigationSql {
		public static final String TABLE = "NAVIGATION";
		public static final String COLUMN_ID = "id";
		public static final String COLUMN_FLOOR_ID = "floor_id";
		public static final String COLUMN_ELEMENT_ID = "element_id";
		public static final String COLUMN_COORDINATE = "coordinate";
		public static final String COLUMN_UPDATED = "updated";

		public static final int INDEX_COLUMN_ID = 0;
		public static final int INDEX_COLUMN_FLOOR_ID = 1;
		public static final int INDEX_COLUMN_ELEMENT_ID = 2;
		public static final int INDEX_COLUMN_COORDINATE = 3;
		public static final int INDEX_COLUMN_UPDATED = 4;

		private static final String DATABASE_CREATE = String.format("CREATE TABLE %s (%s MEDIUMINT PRIMARY KEY, "
				+ "%s MEDIUMINT, %s MEDIUMINT, %s VARCHAR(255), %s INT)", TABLE, COLUMN_ID,
				COLUMN_FLOOR_ID, COLUMN_ELEMENT_ID, COLUMN_COORDINATE, COLUMN_UPDATED);
	}

	private static final String DATABASE_NAME = "campusguide_%d.db";
	private static final int DATABASE_VERSION = 17;

	public SQLiteHelper(Context context, int mode) {
		super(context, String.format(DATABASE_NAME, mode), null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(FacilitySql.DATABASE_CREATE);
		database.execSQL(BuildingSql.DATABASE_CREATE);
		database.execSQL(FloorSql.DATABASE_CREATE);
		database.execSQL(ElementSql.DATABASE_CREATE);
		database.execSQL(NavigationSql.DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(SQLiteHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion
				+ ", which will destroy all old data");

		String[] tables = { FacilitySql.TABLE, BuildingSql.TABLE, FloorSql.TABLE, ElementSql.TABLE, NavigationSql.TABLE };
		for (String table : tables) {
			db.execSQL(String.format("DROP TABLE IF EXISTS %s", table));
		}
		onCreate(db);
	}

}
