package com.skarbo.campusguide.mapper.view.map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.skarbo.campusguide.mapper.model.Element;
import com.skarbo.campusguide.mapper.util.CanvasUtil;
import com.skarbo.campusguide.mapper.util.Coordinates;
import com.skarbo.campusguide.mapper.util.PolygonShape;
import com.skarbo.campusguide.mapper.view.IsViewportChild;

public class BuildingElementMapView extends View implements IsViewportChild {

	private static final String TAG = BuildingElementMapView.class.getSimpleName();

	private static PolygonShape ELEMENT_POLYGON = new PolygonShape();
	private static Paint ELEMENT_LABEL = new Paint(Paint.ANTI_ALIAS_FLAG);

	{

		// ELEMENT POLYGON

		// Border
		ELEMENT_POLYGON.border.paint.setAntiAlias(true);
		ELEMENT_POLYGON.border.paint.setColor(Color.parseColor("#BD8A63"));
		ELEMENT_POLYGON.border.paint.setStyle(Paint.Style.STROKE);
		ELEMENT_POLYGON.border.paint.setStrokeWidth(2);

		// Fill
		ELEMENT_POLYGON.fill.paint.setAntiAlias(true);
		ELEMENT_POLYGON.fill.paint.setColor(Color.parseColor("#FFFFDE"));
		ELEMENT_POLYGON.fill.paint.setStyle(Paint.Style.FILL);

		// /ELEMENT POLYGON

		// ELEMENT LABEL

		ELEMENT_LABEL.setStyle(Paint.Style.FILL);
		ELEMENT_LABEL.setColor(Color.BLACK);
		ELEMENT_LABEL.setTextSize(14);
		ELEMENT_LABEL.setTypeface(Typeface.create("Verdana,Arial,Helvetica,sans-serif", Typeface.NORMAL));

		// /ELEMENT LABEL

	}

	private Element element;
	private IsViewportChild viewportChildParent;

	public BuildingElementMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PointF getViewportPoint(float x, float y) {
		if (this.viewportChildParent == null)
			Log.d(TAG, "ViewportChildParent null");
		return this.viewportChildParent != null ? this.viewportChildParent.getViewportPoint(x, y) : new PointF(x, y);
	}

	public void loadElement(Element element) {
		this.element = element;
		Log.d(TAG, "LoadElement: " + this.element.getId());

		ViewCompat.postInvalidateOnAnimation(this);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		if (getParent() instanceof IsViewportChild)
			this.viewportChildParent = (IsViewportChild) getParent();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (this.element == null)
			return;

		// Set transparent color
		canvas.drawColor(Color.TRANSPARENT);

		// ELEMENT

		for (int i = 0; i < this.element.getCoordinates().size(); i++) {
			Coordinates coordinates = this.element.getCoordinates().get(i);
			for (int j = 0; j < coordinates.getCoordinates().size(); j++) {
				if (j == 0) {
					ELEMENT_POLYGON.border.path.moveTo(coordinates.getCoordinates().get(j).x, coordinates
							.getCoordinates().get(j).y);
					ELEMENT_POLYGON.fill.path.moveTo(coordinates.getCoordinates().get(j).x, coordinates
							.getCoordinates().get(j).y);
				} else {
					ELEMENT_POLYGON.border.path.lineTo(coordinates.getCoordinates().get(j).x, coordinates
							.getCoordinates().get(j).y);
					ELEMENT_POLYGON.fill.path.lineTo(coordinates.getCoordinates().get(j).x, coordinates
							.getCoordinates().get(j).y);
				}
			}

			if (coordinates.getCoordinates().size() > 0) {
				ELEMENT_POLYGON.border.path.lineTo(coordinates.getCoordinates().get(0).x, coordinates.getCoordinates()
						.get(0).y);
				ELEMENT_POLYGON.fill.path.lineTo(coordinates.getCoordinates().get(0).x, coordinates.getCoordinates()
						.get(0).y);
			}

		}

		ELEMENT_POLYGON.border.path.close();
		ELEMENT_POLYGON.fill.path.close();

		canvas.drawPath(ELEMENT_POLYGON.border.path, ELEMENT_POLYGON.border.paint);
		canvas.drawPath(ELEMENT_POLYGON.fill.path, ELEMENT_POLYGON.fill.paint);

		// // ... LABEL
		//
		// for (Element element : this.elements) {
		//
		// for (int i = 0; i < element.getCoordinates().size(); i++) {
		// Coordinates coordinates = element.getCoordinates().get(i);
		// PointF centerPoint = coordinates.getCenterPoint();
		// if (centerPoint != null &&
		// !element.getName().equalsIgnoreCase("")) {
		// canvas.drawText(element.getName(), centerPoint.x, centerPoint.y,
		// elementLabelPaint);
		// }
		// }
		// }
		//
		// // ... /LABEL

		// /ELEMENT
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			PointF relativePoint = getViewportPoint(event.getX(), event.getY());
			boolean contains = contains(relativePoint);
			Log.d(TAG,
					"ElementView.onTouchEvent: " + this.element.getId() + " contains: " + contains + " ("
							+ event.getX() + ", " + event.getY() + ", Relative: " + relativePoint.toString() + ")");
		}
		return super.onTouchEvent(event);
	}

	public boolean contains(PointF point) {
		boolean result = false;
		for (Coordinates coordinates : this.element.getCoordinates()) {
			result = result || CanvasUtil.polygonContains(coordinates.getCoordinates(), point);
		}
		return result;
	}

}