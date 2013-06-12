package com.skarbo.campusguide.mapper.dao.web.rest;

import android.content.Context;
import android.util.Log;

import com.skarbo.campusguide.mapper.dao.web.FloorWebDao;
import com.skarbo.campusguide.mapper.model.Factory;
import com.skarbo.campusguide.mapper.model.Floor;

public class FloorRestWebDao extends StandardRestWebDao<Floor> implements FloorWebDao {

	private static final String TAG = FloorRestWebDao.class.getSimpleName();
	private static final String CONTROLLER = "buildingfloors";
	private static final String COMMAND_GET_MAIN = "main";

	public FloorRestWebDao(Context context, int mode, Factory<Floor> factory) {
		super(context, CONTROLLER, mode, factory);
	}

	public void retrieveMain(Integer[] buildingIds, StandardWebCallback<Floor> callback) {
		new GetMainStandardRestTask(callback).execute(buildingIds);
	}

	// ... CLASS

	class GetMainStandardRestTask extends StandardRestTask {

		public GetMainStandardRestTask(StandardWebCallback<Floor> callback) {
			super(callback);
		}

		@Override
		protected StandardWebResult<Floor> doInBackground(Integer... ids) {
			try {
				if (ids.length > 0) {
					return retrieve(COMMAND_GET_MAIN, ids);
				}
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
			}
			return null;
		}

	}

	// ... /CLASS

}
