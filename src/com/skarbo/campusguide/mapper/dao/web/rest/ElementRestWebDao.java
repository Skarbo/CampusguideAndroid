package com.skarbo.campusguide.mapper.dao.web.rest;

import android.content.Context;
import android.util.Log;

import com.skarbo.campusguide.mapper.model.Element;

public class ElementRestWebDao extends StandardRestWebDao<Element> {

	private static final String TAG = ElementRestWebDao.class.getSimpleName();
	private static final String CONTROLLER = "buildingelements";
	private static final String COMMAND_GET_BUILDING = "building";

	public ElementRestWebDao(Context context, int mode) {
		super(context, CONTROLLER, mode, new Element.ElementFactory());
	}

	public void getBuilding(int buildingId, StandardWebCallback<Element> callback) {
		new GetBuildingStandardRestTask(callback).execute(buildingId);
	}

	// ... CLASS

	class GetBuildingStandardRestTask extends StandardRestTask {

		public GetBuildingStandardRestTask(StandardWebCallback<Element> callback) {
			super(callback);
		}

		@Override
		protected StandardWebResult<Element> doInBackground(Integer... ids) {
			try {
				return retrieve(COMMAND_GET_BUILDING, ids);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
			}
			return null;
		}

	}

	// ... /CLASS

}
