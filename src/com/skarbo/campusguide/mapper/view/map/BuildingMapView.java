package com.skarbo.campusguide.mapper.view.map;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;

import com.skarbo.campusguide.mapper.model.Element;
import com.skarbo.campusguide.mapper.model.Floor;
import com.skarbo.campusguide.mapper.model.adapter.ModelAdapter;
import com.skarbo.campusguide.mapper.util.CanvasUtil;
import com.skarbo.campusguide.mapper.util.Coordinates;
import com.skarbo.campusguide.mapper.view.IsViewport;
import com.skarbo.campusguide.mapper.view.IsViewportChild;

public class BuildingMapView extends ViewGroup implements IsViewportChild {

	private static final String TAG = BuildingMapView.class.getSimpleName();

	private static final int LAYER_FLAGS = Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG
			| Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG | Canvas.CLIP_TO_LAYER_SAVE_FLAG;

	private Floor floor = null;
	private ModelAdapter<Element> elements = null;

	private IsViewport viewport;
	private Handler mHandler;

	public BuildingMapView(Context context, AttributeSet attrs) {
		super(context, attrs);

		setWillNotDraw(false);
		mHandler = new Handler();
	}

	public void loadFloor(Floor floor, ModelAdapter<Element> elements) {
		Log.d(TAG, "LoadFloor: " + floor.getId() + ", " + elements.size());
		this.floor = floor;
		this.elements = elements;

		// removeAllViews();

		BuildingFloorMapView floorViewGroup = new BuildingFloorMapView(getContext(), null);
		floorViewGroup.loadFloor(floor, elements);

		RectF buildingBounds = getBuildingBounds();
		Log.d(TAG, "LoadFloor: Building bounds: " + buildingBounds.left + ", " + buildingBounds.right + ", "
				+ buildingBounds.top + ", " + buildingBounds.bottom);

		this.addView(floorViewGroup);
		// requestLayout();
		// invalidate();
		mHandler.post(new Runnable() {
			public void run() {
				Log.d(TAG, "LoadFloor: Runnable");
				requestLayout();
			}
		});
	}

	// ... ON

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		if (getParent() instanceof IsViewport)
			this.viewport = (IsViewport) getParent();
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
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (this.floor != null) {
			RectF buildingBounds = getBuildingBounds();
			Log.d(TAG, "OnMeasure: Building bounds: " + buildingBounds.left + ", " + buildingBounds.right + ", "
					+ buildingBounds.top + ", " + buildingBounds.bottom);
			setMeasuredDimension((int) buildingBounds.right, (int) buildingBounds.bottom);
		} else {
			setMeasuredDimension(widthMeasureSpec + 200, heightMeasureSpec);
		}
		Log.d(TAG, "OnMeasure: " + getMeasuredWidth() + ", " + getMeasuredHeight());
	}

	// ... /ON

	public PointF getViewportPoint(float x, float y) {
		if (this.viewport == null)
			Log.d(TAG, "Viewport null");
		return this.viewport != null ? this.viewport.getRelativePoint(x, y) : new PointF(x, y);
	}

	private static float getFitScale(int displayHeight, int displayWidth, float boundHeight, float boundWidth) {
		float scaleY = (float) displayHeight / (float) boundHeight;
		float scaleX = (float) displayWidth / (float) boundWidth;
		float scale = Math.min(scaleY, scaleX);
		return scale > 0f ? scale : 0f;
	}

	private static PointF getFitTranslate(int displayHeight, int displayWidth, float boundHeight, float boundWidth,
			float scale) {
		float boundHeightNew = boundHeight * scale, boundWidthNew = boundWidth * scale;
		float translateHeight = Math.max((displayHeight - boundHeightNew) / 2, 0f), translateWidth = Math.max(
				(displayWidth - boundWidthNew) / 2, 0f);
		return new PointF(translateWidth, translateHeight);
	}

	private static PointF getPointsSize(List<Coordinates> points) {
		if (points.size() == 0 || points.get(0).getCoordinates().isEmpty()) {
			return new PointF(0f, 0f);
		}

		PointF point = points.get(0).getCoordinates().get(0);
		float width = point.x, height = point.y;
		for (int i = 0; i < points.size(); i++) {
			for (int j = 1; j < points.get(i).getCoordinates().size(); j++) {
				point = points.get(i).getCoordinates().get(j);

				// Width
				if (point.x > width) {
					width = point.x;
				}
				// Height
				if (point.y > height) {
					height = point.y;
				}
			}
		}

		return new PointF(width, height);
	}

	private RectF getBuildingBounds() {
		RectF[] bounds = new RectF[this.floor.getCoordinates().size()];
		for (int i = 0; i < this.floor.getCoordinates().size(); i++) {
			bounds[i] = CanvasUtil.getBounds(this.floor.getCoordinates().get(i).getCoordinates());
		}
		return CanvasUtil.getBounds(bounds);
	}
}
