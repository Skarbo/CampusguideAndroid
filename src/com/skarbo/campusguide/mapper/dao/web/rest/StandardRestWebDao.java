package com.skarbo.campusguide.mapper.dao.web.rest;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.skarbo.campusguide.mapper.MainActivity;
import com.skarbo.campusguide.mapper.dao.web.StandardWebDao;
import com.skarbo.campusguide.mapper.model.Factory;
import com.skarbo.campusguide.mapper.model.Model;
import com.skarbo.campusguide.mapper.model.adapter.ModelAdapter;
import com.skarbo.campusguide.mapper.util.RestClient;
import com.skarbo.campusguide.mapper.util.RestClient.RequestMethod;
import com.skarbo.campusguide.mapper.util.Util;

public class StandardRestWebDao<E extends Model> implements StandardWebDao<E> {

	private static final String TAG = StandardRestWebDao.class.getSimpleName();
	private static final String COMMAND_GET = "get";
	private static final String COMMAND_GET_FOREIGN = "foreign";

	private Context context;
	private String controller;
	private int mode;
	private Factory<E> factory;

	public StandardRestWebDao(Context context, String controller, int mode, Factory<E> factory) {
		this.context = context;
		this.controller = controller;
		this.mode = mode;
		this.factory = factory;
	}

	public void retrieveAll(StandardWebCallback<E> callback) {
		new GetAllStandardRestTask(callback).execute();
	}

	public void retrieve(int id, StandardWebCallback<E> callback) {
		new GetStandardRestTask(callback).execute(id);
	}

	public void retrieveForeign(Integer[] foreignIds, StandardWebCallback<E> callback) {
		new GetForeignStandardRestTask(callback).execute(foreignIds);
	}

	// ... CLASS

	// ... ... REST TASK

	abstract class StandardRestTask extends AsyncTask<Integer, Integer, StandardWebResult<E>> {

		public static final String URI = "http://%s:8008/KrisSkarbo/CampusGuide/api_rest.php?/%s/%s/%s&mode=%d";
		public static final String URI_ID_SPLITTER = "_";

		protected StandardWebCallback<E> callback;

		public StandardRestTask(StandardWebCallback<E> callback) {
			this.callback = callback;
		}

		protected void onPostExecute(StandardWebResult<E> result) {
			if (result != null && this.callback != null) {
				this.callback.onResult(result, true);
			}
		};

		// ... REST HANDLER

		private String createUri(String command, String args, int mode) {
			String serverIp = PreferenceManager.getDefaultSharedPreferences(context).getString("server_ip", "");
			return String.format(URI, serverIp, controller, command, args, mode);
		}

		public StandardWebResult<E> retrieveUri(String uri) throws Exception {
			Log.d(TAG, "Retrieve: " + uri);
			RestClient client = new RestClient(uri);
			client.execute(RequestMethod.GET);
			Log.d(TAG, "Response: " + uri + ", " + client.getResponse().length() + ", " + client.getResponseCode()
					+ ", " + client.getErrorMessage());

			StandardWebResult<E> webResult = parseJSONResponse(client.getResponse());
			return webResult;
		}

		public StandardWebResult<E> retrieve(String command) throws Exception {
			String uri = createUri(command, "", mode);
			return retrieveUri(uri);
		}

		public StandardWebResult<E> retrieve(String command, String args) throws Exception {
			String uri = createUri(command, args, mode);
			return retrieveUri(uri);
		}

		public StandardWebResult<E> retrieve(String command, Object[] args) throws Exception {
			String uri = createUri(command, Util.implode(args, URI_ID_SPLITTER), mode);
			return retrieveUri(uri);
		}

		private StandardWebResult<E> parseJSONResponse(String jsonResponse) {
			E single = null;
			ModelAdapter<E> models = new ModelAdapter<E>();

			if (jsonResponse != null && jsonResponse != "") {
				try {
					JSONObject json = new JSONObject(jsonResponse);

					// Single
					try {
						if (!json.isNull("single")) {
							single = singleParseJSONResponse(json.getJSONObject("single"));
						}
					} catch (JSONException e) {
						Log.w(TAG, e.getMessage() + "\n" + jsonResponse);
					}

					// List
					try {
						models = listParseJSONResponse(json.getJSONObject("list"));
					} catch (JSONException e) {
						Log.w(TAG, e.getMessage() + "\n" + jsonResponse);
					}
				} catch (JSONException e) {
					Log.w(TAG, e.getMessage() + "\n" + jsonResponse);
				}
			} else {
				Log.w(TAG, "Parse JSON response is empty");
			}

			return new StandardWebResult<E>(single, models);
		}

		private E singleParseJSONResponse(JSONObject singleJson) {
			E model = null;

			if (singleJson == null)
				return model;
			return factory.generate(singleJson);
		}

		private ModelAdapter<E> listParseJSONResponse(JSONObject listJson) {
			ModelAdapter<E> models = new ModelAdapter<E>();
			if (listJson == null)
				return models;

			try {
				@SuppressWarnings("rawtypes")
				Iterator iter = listJson.keys();
				while (iter.hasNext()) {
					String key = (String) iter.next();
					models.add((E) factory.generate(listJson.getJSONObject(key)));
				}
			} catch (JSONException e) {
				Log.w(TAG, e.getMessage() + "\n" + listJson.toString());
			}
			return models;
		}

		// ... /REST HANDLER
	}

	class GetAllStandardRestTask extends StandardRestTask {

		public GetAllStandardRestTask(StandardWebCallback<E> callback) {
			super(callback);
		}

		@Override
		protected StandardWebResult<E> doInBackground(Integer... ids) {
			try {
				return retrieve(COMMAND_GET);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
			}
			return null;
		}

	}

	class GetStandardRestTask extends StandardRestTask {

		public GetStandardRestTask(StandardWebCallback<E> callback) {
			super(callback);
		}

		@Override
		protected StandardWebResult<E> doInBackground(Integer... ids) {
			try {
				if (ids.length > 0) {
					return retrieve(COMMAND_GET, String.valueOf(ids[0]));
				}
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
			}
			return null;
		}

	}

	class GetForeignStandardRestTask extends StandardRestTask {

		public GetForeignStandardRestTask(StandardWebCallback<E> callback) {
			super(callback);
		}

		@Override
		protected StandardWebResult<E> doInBackground(Integer... ids) {
			try {
				if (ids.length > 0) {
					return retrieve(COMMAND_GET_FOREIGN, ids);
				}
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
			}
			return null;
		}

	}

	// ... ... /REST TASK

	// ... /CLASS

}
