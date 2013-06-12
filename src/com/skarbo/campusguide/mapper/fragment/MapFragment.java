package com.skarbo.campusguide.mapper.fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.skarbo.campusguide.mapper.BuildingActivity;
import com.skarbo.campusguide.mapper.MainActivity;
import com.skarbo.campusguide.mapper.R;
import com.skarbo.campusguide.mapper.handler.CampusguideHandler;
import com.skarbo.campusguide.mapper.model.Building;
import com.skarbo.campusguide.mapper.model.Facility;
import com.skarbo.campusguide.mapper.model.adapter.ModelAdapter;
import com.skarbo.campusguide.mapper.util.Util;

public class MapFragment extends SupportMapFragment implements OnInfoWindowClickListener {

	private static final String TAG = MapFragment.class.getSimpleName();
	private static final double latitude = 60.384272;
	private static final double longitude = 5.333134;

	private CampusguideHandler campusguideHandler;
	private Map<Marker, Integer> buildingMarkers;
	private List<Polygon> buildingPolygon;

	private DataSetObserver facilitiesObserver = new DataSetObserver() {
		@Override
		public void onChanged() {
			Log.d(TAG, "Facilities changed");
			doRefresh();
		}
	};

	private DataSetObserver buildingsObserver = new DataSetObserver() {
		@Override
		public void onChanged() {
			Log.d(TAG, "Buildings changed");
			doRefresh();
		}
	};

	public MapFragment() {
		super();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Campusguide handler
		this.campusguideHandler = ((MainActivity) getActivity()).getCampusguideHandler();

		ModelAdapter<Facility> facilities = this.campusguideHandler.getFacilityProxy().getAll();
		ModelAdapter<Building> buildings = this.campusguideHandler.getBuildingProxy().getForeign(facilities.getIds());
		// this.campusguideHandler.getFloorProxy().getMain(buildings.getIds());

		buildingMarkers = new HashMap<Marker, Integer>();
		buildingPolygon = new ArrayList<Polygon>();

		if (getMap() != null) {
			getMap().setMyLocationEnabled(true);
			getMap().getUiSettings().setCompassEnabled(false);
			getMap().getUiSettings().setMyLocationButtonEnabled(false);
			getMap().getUiSettings().setRotateGesturesEnabled(false);

			getMap().setOnInfoWindowClickListener(this);
			getMap().setOnCameraChangeListener(new OnCameraChangeListener() {
				public void onCameraChange(CameraPosition position) {
					Log.d(TAG, "Camera zoom: " + position.zoom);
				}
			});
			getMap().setOnMapClickListener(new OnMapClickListener() {
				public void onMapClick(LatLng point) {

				}
			});
		}

		// SVG directionsIconSvg =
		// SVGParser.getSVGFromResource(getActivity().getResources(),
		// R.raw.icon_directions);
		// SVG directionsIconSvg =
		// SVGParser.getSVGFromAsset(getActivity().getAssets(),
		// "svg/icon_directions.png");
		// directionsDrawable = directionsIconSvg.createPictureDrawable();

		doRefresh();
		if (savedInstanceState == null) {
			doMoveToStart();
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		// Campusguide handler
		this.campusguideHandler.getFacilities().registerDataSetObserver(facilitiesObserver);
		this.campusguideHandler.getBuildings().registerDataSetObserver(buildingsObserver);
	}

	@Override
	public void onPause() {
		super.onPause();

		// Campusguide handler
		this.campusguideHandler.getFacilities().unregisterDataSetObserver(facilitiesObserver);
		this.campusguideHandler.getBuildings().unregisterDataSetObserver(buildingsObserver);
	}

	public void onInfoWindowClick(Marker marker) {
		if (buildingMarkers.containsKey(marker)) {
			Integer buildingId = buildingMarkers.get(marker);
			if (buildingId != null) {
				Building building = this.campusguideHandler.getBuildings().getId(buildingId);
				BuildingDialog buildingDialog = BuildingDialog.newInstance(building);
				buildingDialog.show(getFragmentManager(), "building_dialog");
			}
		}
	}

	private void doRefresh() {
		if (getMap() == null) {
			Log.d(TAG, "GoogleMap not retrieved, no refresh");
			return;
		}
		Log.d(TAG, "Do refresh");

		// Remove building markers
		for (Marker marker : buildingMarkers.keySet()) {
			marker.remove();
		}
		buildingMarkers.clear();

		// Remove building polygons
		for (Polygon polyline : buildingPolygon) {
			polyline.remove();
		}
		buildingPolygon.clear();

		Facility facility = null;
		for (Building building : this.campusguideHandler.getBuildings()) {
			Log.d(TAG, "Building: " + building.toString());
			facility = this.campusguideHandler.getFacilities().getId(building.getFacilityId());
			// Add building marker
			if (building.getPosition() != null && building.getPosition().length >= 2) {
				buildingMarkers.put(
						getMap().addMarker(
								new MarkerOptions()
										.position(new LatLng(building.getPosition()[0], building.getPosition()[1]))
										.title(building.getName()).snippet(facility.getName())
										.icon(BitmapDescriptorFactory.fromResource(R.drawable.logo_hib))),
						building.getId());
			} else if (building.getLocation() != null && building.getLocation().length >= 2) {
				buildingMarkers.put(
						getMap().addMarker(
								new MarkerOptions()
										.position(new LatLng(building.getLocation()[0], building.getLocation()[1]))
										.title(building.getName()).snippet(facility.getName())
										.icon(BitmapDescriptorFactory.fromResource(R.drawable.logo_hib))),
						building.getId());
			}

			// Add building polygon
			Log.d(TAG, "Overlay: " + Arrays.toString(building.getOverlay()));
			if (building.getOverlay() != null && building.getOverlay().length > 0) {
				for (String overlay : building.getOverlay()) {
					if (overlay != null) {
						List<LatLng> decodePoly = Util.decodePoly(overlay);
						Log.d(TAG, "Poly: " + Arrays.toString(decodePoly.toArray()));
						if (!decodePoly.isEmpty()) {
							PolygonOptions polygonOptions = new PolygonOptions();
							// polygonOptions.addAll(decodePoly);
							for (int i = decodePoly.size() - 1; i >= 0; i--) {
								polygonOptions.add(decodePoly.get(i));
							}
							polygonOptions.strokeColor(Color.parseColor("#00000000"));
							polygonOptions.fillColor(Color.parseColor("#99134F5C"));
							Polygon polygon = getMap().addPolygon(polygonOptions);
							buildingPolygon.add(polygon);
						}
					}
				}
			}
		}

		// Add building polygon
		// for (Floor floor : this.campusguideHandler.getFloors()) {
		// building =
		// this.campusguideHandler.getBuildings().getId(floor.getBuildingId());
		// if (building.getPosition().length >= 4 && floor.isMain() &&
		// !floor.getCoordinates().isEmpty()) {
		// for (Coordinate[] coordinates : floor.getCoordinates()) {
		//
		// PointF leftTop = new PointF(1981.3f,293f);
		// PointF rightTop = new PointF(3920.3f,434.3f);
		//
		// float xDiff = rightTop.x - leftTop.x;
		// float yDiff = rightTop.y - leftTop.y;
		// double latDiff = building.getPosition()[2][0] -
		// building.getPosition()[1][0];
		// double longDiff = building.getPosition()[2][1] -
		// building.getPosition()[1][1];
		// float latExp = (float) latDiff / (float) xDiff; //
		// 60.384811+(4.861407249466951*10^-7)*50
		// float longExp = (float) longDiff / (float) yDiff;
		//
		// Log.d(TAG, String.format("Building: %d, " +
		// "LeftTop: %f,%f, RightTop: %f,%f, " +
		// "LatLonLeft: %f,%f, LatLonRight: %f,%f, " +
		// "xDiff: %f, yDiff: %f, " +
		// "latDiff: %f, longDiff: %f, " +
		// "latExp: %f, longExp: %f",
		// building.getId(),
		// leftTop.x, leftTop.y, rightTop.x, rightTop.y,
		// building.getPosition()[1][0], building.getPosition()[1][1],
		// building.getPosition()[2][0], building.getPosition()[2][1],
		// xDiff, yDiff,
		// latDiff, longDiff,
		// latExp, longExp));
		//
		// PolygonOptions polygonOptions = new PolygonOptions();
		// for (Coordinate coordinate : coordinates) {
		// if (coordinate != null) {
		// LatLng latLng = new LatLng(
		// (double) Math.round((building.getPosition()[1][0] + (latExp *
		// (coordinate.x - leftTop.x))) * 100000) / 100000,
		// (double) Math.round((building.getPosition()[1][1] + (longExp *
		// (coordinate.y - leftTop.y))) * 100000) / 100000);
		// Log.d(TAG, String.format("Floor coordinate: %f,%f, LatLong: %s",
		// coordinate.x, coordinate.y, latLng.toString()));
		// polygonOptions.add(latLng);
		// }
		// }
		// polygonOptions.strokeColor(Color.RED);
		// polygonOptions.fillColor(Color.BLUE);
		// buildingPolygon.add(getMap().addPolygon(polygonOptions));
		// }
		// }
		// }
	}

	private void doMoveToStart() {
		if (getMap() == null) {
			return;
		}
		Log.d(TAG, "Moving to start");

		CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latitude, longitude))
				.zoom(15.5f).build();
		CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
		getMap().moveCamera(cameraUpdate);
	}

	// ... CLASS

	public static class BuildingDialog extends DialogFragment implements OnItemClickListener {
		private static final String ARG_BUILDING = "building";

		public enum TYPE {
			DIRECTIONS, BUILDING
		}

		public static BuildingDialog newInstance(Building building) {
			BuildingDialog buildingDialog = new BuildingDialog();

			Bundle bundle = new Bundle();
			bundle.putSerializable(ARG_BUILDING, building);
			buildingDialog.setArguments(bundle);

			return buildingDialog;
		}

		private Building building;
		private ListView buildingListView;
		private BuildingArrayAdapter buildingArrayAdapter;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			building = (Building) getArguments().getSerializable(ARG_BUILDING);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.fragment_building_dialog, container, false);

			getDialog().setTitle(building.getName());

			buildingListView = (ListView) view.findViewById(R.id.building_dialog_list_view);

			List<TYPE> objects = new ArrayList<TYPE>();

			if (building.getAddress().length > 0)
				objects.add(TYPE.DIRECTIONS);
			if (building.getFloors() > 0)
				objects.add(TYPE.BUILDING);

			buildingArrayAdapter = new BuildingArrayAdapter(getActivity(), R.layout.list_view_building_dialog_row,
					objects);
			buildingListView.setAdapter(buildingArrayAdapter);
			buildingListView.setOnItemClickListener(this);

			return view;
		}

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			switch (buildingArrayAdapter.getItem(arg2)) {
			case BUILDING:
				Intent buildingIntent = new Intent(getActivity(), BuildingActivity.class);
				buildingIntent.putExtra(BuildingActivity.ARG_BUILDING_ID, building.getId());
				startActivity(buildingIntent);
				break;
			}
		}

		class BuildingArrayAdapter extends ArrayAdapter<TYPE> {

			private LayoutInflater layoutInfalter;

			public BuildingArrayAdapter(Context context, int textViewResourceId, List<TYPE> objects) {
				super(context, textViewResourceId, objects);
				layoutInfalter = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = convertView;
				TYPE type = getItem(position);

				if (type != null) {
					view = layoutInfalter.inflate(R.layout.list_view_building_dialog_row, null);

					ImageView iconImageView = (ImageView) view.findViewById(R.id.icon_list_view_building_dialog);
					TextView textTextView = (TextView) view.findViewById(R.id.text_list_view_building_dialog);

					String text = "";
					int icon = 0;
					switch (type) {
					case DIRECTIONS:
						text = "Take me there";
						icon = R.drawable.icon_directions;
						break;
					case BUILDING:
						text = "See building";
						icon = R.drawable.icon_building;
						break;
					}

					// iconImageView.setImageDrawable(icon);
					iconImageView.setImageResource(icon);
					textTextView.setText(text);
				}

				return view;
			}

		}

	}

	class MapInfoWindowAdapter implements InfoWindowAdapter {

		public View getInfoContents(Marker marker) {
			return null;
		}

		public View getInfoWindow(Marker marker) {
			return null;
		}

	}

	// ... /CLASS

}
