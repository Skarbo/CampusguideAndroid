package com.skarbo.campusguide.mapper.view.map;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;

import com.skarbo.campusguide.mapper.model.Element;
import com.skarbo.campusguide.mapper.model.Floor;
import com.skarbo.campusguide.mapper.util.PolygonShape;
import com.skarbo.campusguide.mapper.view.IsViewportChild;

public class BuildingFloorMapView extends ViewGroup implements IsViewportChild {

	private static final String TAG = BuildingFloorMapView.class.getSimpleName();

	private static PolygonShape FLOOR_POLYGON = new PolygonShape();

	{

		// FLOOR POLYGON

		// Border
		FLOOR_POLYGON.border.paint.setAntiAlias(true);
		FLOOR_POLYGON.border.paint.setColor(Color.parseColor("#CECBBD")); // #DEDFDE
		FLOOR_POLYGON.border.paint.setStyle(Paint.Style.STROKE);
		FLOOR_POLYGON.border.paint.setStrokeWidth(2);

		// Fill
		FLOOR_POLYGON.fill.paint.setAntiAlias(true);
		FLOOR_POLYGON.fill.paint.setColor(Color.parseColor("#DEDBD6"));
		FLOOR_POLYGON.fill.paint.setStyle(Paint.Style.FILL);

		// /FLOOR POLYGON

	}

	private IsViewportChild viewportChildParent;

	private Floor floor;
	private List<Element> elements;

	public BuildingFloorMapView(Context context, AttributeSet attrs) {
		super(context, attrs);

		setWillNotDraw(false);
	}

	public PointF getViewportPoint(float x, float y) {
		if (this.viewportChildParent == null)
			Log.d(TAG, "ViewportChildParent null");
		return this.viewportChildParent != null ? this.viewportChildParent.getViewportPoint(x, y) : new PointF(x, y);
	}

	public void loadFloor(Floor floor, List<Element> elements) {
		Log.d(TAG, "LoadFloor: " + floor.getId() + ", " + elements.size());
		this.floor = floor;
		this.elements = elements;

		// removeAllViews();

		for (Element element : elements) {
			BuildingElementMapView elementView = new BuildingElementMapView(getContext(), null);
			elementView.loadElement(element);
			addView(elementView);
		}

		requestLayout();
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		if (getParent() instanceof IsViewportChild)
			this.viewportChildParent = (IsViewportChild) getParent();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		Log.d(TAG, "OnLayout: " + l + ", " + t + ", " + r + ", " + b + ": " + getChildCount());
		for (int i = 0; i < getChildCount(); i++) {
			getChildAt(i).layout(l, t, r, b);
		}
		ViewCompat.postInvalidateOnAnimation(this);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (this.floor == null)
			return;

		// Set transparent color
		canvas.drawColor(Color.TRANSPARENT);

		// FLOOR

		for (int i = 0; i < this.floor.getCoordinates().size(); i++) {
			for (int j = 0; j < this.floor.getCoordinates().get(i).getCoordinates().size(); j++) {
				PointF coordinate = this.floor.getCoordinates().get(i).getCoordinates().get(j);
				if (j == 0) {
					FLOOR_POLYGON.border.path.moveTo(coordinate.x, coordinate.y);
					FLOOR_POLYGON.fill.path.moveTo(coordinate.x, coordinate.y);
				} else {
					FLOOR_POLYGON.border.path.lineTo(coordinate.x, coordinate.y);
					FLOOR_POLYGON.fill.path.lineTo(coordinate.x, coordinate.y);
				}
			}

			if (this.floor.getCoordinates().get(i).getCoordinates().size() > 0) {
				PointF coordinate = this.floor.getCoordinates().get(i).getCoordinates().get(0);
				FLOOR_POLYGON.border.path.lineTo(coordinate.x, coordinate.y);
				FLOOR_POLYGON.fill.path.lineTo(coordinate.x, coordinate.y);
			}
		}

		// for (int i = 0; i < BUILDING.length; i++) {
		// if (i == 0) {
		// floorBorderPath.moveTo(BUILDING[i].x, BUILDING[i].y);
		// } else {
		// floorBorderPath.lineTo(BUILDING[i].x, BUILDING[i].y);
		// }
		// }

		FLOOR_POLYGON.border.path.close();
		FLOOR_POLYGON.fill.path.close();

		canvas.drawPath(FLOOR_POLYGON.border.path, FLOOR_POLYGON.border.paint);
		canvas.drawPath(FLOOR_POLYGON.fill.path, FLOOR_POLYGON.fill.paint);

		// /FLOOR<
	}
}
