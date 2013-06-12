package com.skarbo.campusguide.mapper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.skarbo.campusguide.mapper.R;
import com.skarbo.campusguide.mapper.model.Building;
import com.skarbo.campusguide.mapper.model.Facility;
import com.skarbo.campusguide.mapper.model.adapter.ModelAdapter;

public class FacilityListAdapter extends BaseExpandableListAdapter {

	private static final String TAG = "FacilityListAdapter";
	private Context context;
	private ModelAdapter<Facility> facilities;
	private LayoutInflater layoutInflater;

	public FacilityListAdapter(Context context, ModelAdapter<Facility> facilities) {
		this.context = context;
		this.facilities = facilities;
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public Object getGroup(int groupPosition) {
		return this.facilities.get(groupPosition);
	}

	public Object getChild(int groupPosition, int childPosition) {
		return ((Facility) this.facilities.get(groupPosition)).getBuildings().get(childPosition);
	}

	public int getChildrenCount(int groupPosition) {
		return ((Facility) this.facilities.get(groupPosition)).getBuildings().size();
	}

	public int getGroupCount() {
		return this.facilities.size();
	}

	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		Facility facility = (Facility) getGroup(groupPosition);
		if (facility == null)
			return convertView;
		if (convertView == null) {
			LayoutInflater layoutInflater = (LayoutInflater) this.context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.facility_row, null);
		}

		TextView facilityNameTextView = (TextView) convertView.findViewById(R.id.facilityName);
		ImageView facilityImageView = (ImageView) convertView.findViewById(R.id.facilityImageView);

		facilityNameTextView.setText(facility.getName() + " (" + facility.getBuildingsCount() + ")");
		return convertView;
	}

	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		Building building = (Building) getChild(groupPosition, childPosition);
		if (building == null)
			return convertView;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.building_row, null);
		}

		TextView buildingNameTextView = (TextView) convertView.findViewById(R.id.buildingName);
		buildingNameTextView.setText(building.getName());

		return convertView;
	}

	public boolean hasStableIds() {
		return true;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
