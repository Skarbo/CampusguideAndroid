package com.skarbo.campusguide.mapper.dao.db;

import java.util.Arrays;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.skarbo.campusguide.mapper.dao.StandardDao;
import com.skarbo.campusguide.mapper.model.Model;
import com.skarbo.campusguide.mapper.model.adapter.ModelAdapter;
import com.skarbo.campusguide.mapper.util.SQLiteHelper;
import com.skarbo.campusguide.mapper.util.Util;

public abstract class StandardDbDao<E extends Model> implements StandardDao<E> {

	private static final String TAG = StandardDbDao.class.getSimpleName();
	protected SQLiteDatabase database;

	public StandardDbDao(SQLiteDatabase database) {
		this.database = database;
	}

	// ... DATABASE

	protected abstract String getTable();

	protected String getColumnId() {
		return SQLiteHelper.COLUMN_ID_DEFAULT;
	}

	protected String getColumnForeign() {
		return getColumnId();
	}

	protected String[] getColumnsAll() {
		return null;
	}

	protected abstract E createModel(Cursor cursor);

	protected Cursor getListCursor() {
		return database.query(getTable(), getColumnsAll(), null, null, null, null, null);
	}

	protected Cursor getSingleCursor(int id) {
		return database.query(getTable(), getColumnsAll(), String.format("%s=?", getColumnId()),
				new String[] { String.valueOf(id) }, null, null, null);
	}

	protected Cursor getForeignCursor(String[] foreignIdsString) {
		String[] selections = new String[foreignIdsString.length];
		Arrays.fill(selections, String.format("%s=?", getColumnForeign()));
		String selection = Util.implode(selections, " OR ");

		return database.query(getTable(), getColumnsAll(), selection, foreignIdsString, null, null, null);
	}

	protected abstract ContentValues createAddEditValues(E single);

	protected ModelAdapter<E> createList(Cursor cursor) {
		ModelAdapter<E> list = new ModelAdapter<E>();

		if (cursor != null) {
			try {

				cursor.moveToFirst();
				while (!cursor.isAfterLast()) {
					E model = createModel(cursor);
					list.add(model);
					cursor.moveToNext();
				}
				cursor.close();
			} finally {
				cursor.close();
			}
		}

		return list;
	}

	// ... /DATABASE

	public ModelAdapter<E> getAll() {
		return createList(getListCursor());
	}

	public E get(int id) {
		Cursor cursor = getSingleCursor(id);
		E single = null;
		if (cursor != null) {
			try {
				if (cursor.moveToFirst())
					single = createModel(cursor);
				cursor.close();
			} finally {
				cursor.close();
			}
		}
		return single;
	}

	public ModelAdapter<E> getForeign(Integer[] foreignIds) {
		String[] foreignIdsString = new String[foreignIds.length];
		for (int i = 0; i < foreignIds.length; i++)
			foreignIdsString[i] = String.valueOf(foreignIds[i]);

		return createList(getForeignCursor(foreignIdsString));
	}

	private boolean isExists(int id) {
		Cursor cursor = getSingleCursor(id);
		boolean exists = cursor.moveToFirst();
		cursor.close();
		return exists;
	}

	public void add(E single) {
		if (single == null)
			return;
		if (isExists(single.getId())) {
			edit(single, single.getId());
		} else {
			ContentValues values = createAddEditValues(single);
			database.insert(getTable(), null, values);
		}
	}

	public void edit(E single, int id) {
		ContentValues values = createAddEditValues(single);
		database.update(getTable(), values, String.format("%s=?", getColumnId()), new String[] { String.valueOf(id) });
	}

	public void addAll(ModelAdapter<E> list) {
		for (E model : list) {
			add(model);
		}
	}

	public void addAll(List<E> list) {
		for (E model : list) {
			add(model);
		}
	}
}
