package com.skarbo.campusguide.mapper.model;

import org.json.JSONObject;

import android.database.Cursor;

public interface Factory<E extends Model> {

	public E generate(JSONObject jsonObject);

	public E generate(Cursor cursor);

}
