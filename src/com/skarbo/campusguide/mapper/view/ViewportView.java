package com.skarbo.campusguide.mapper.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.EdgeEffectCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.OnHierarchyChangeListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Scroller;

public class ViewportView extends ViewGroup implements IsViewport {

	private static final String TAG = ViewportView.class.getSimpleName();

	private static final float ZOOM_AMOUNT = 0.025f;

	private MyScroller scroller;
	private MyZoomer zoomer;
	private GestureDetectorCompat gestureDetector;
	private ScaleGestureDetector scaleGestureDetector;

	private EdgeEffectCompat edgeEffectTop;
	private EdgeEffectCompat edgeEffectBottom;
	private EdgeEffectCompat edgeEffectLeft;
	private EdgeEffectCompat edgeEffectRight;

	private Matrix matrix = new Matrix();
	private float[] matrixValuesBuffer = { 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f };

	public ViewportView(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.scroller = new MyScroller(getContext());
		this.zoomer = new MyZoomer(getContext());
		this.gestureDetector = new GestureDetectorCompat(context, new MyGestureListener());
		this.scaleGestureDetector = new ScaleGestureDetector(context, new MyScaleListener());

		this.edgeEffectTop = new EdgeEffectCompat(context);
		this.edgeEffectBottom = new EdgeEffectCompat(context);
		this.edgeEffectLeft = new EdgeEffectCompat(context);
		this.edgeEffectRight = new EdgeEffectCompat(context);

		setWillNotDraw(false);

	}

	// ... GET

	private float[] getMatrixScale() {
		if (this.matrix == null)
			return new float[] { 1f, 1f };
		this.matrix.getValues(this.matrixValuesBuffer);
		return new float[] { this.matrixValuesBuffer[Matrix.MSCALE_X], this.matrixValuesBuffer[Matrix.MSCALE_Y] };
	}

	private float[] getMatrixTransform() {
		if (this.matrix == null)
			return new float[] { 0f, 0f };
		this.matrix.getValues(this.matrixValuesBuffer);
		return new float[] { this.matrixValuesBuffer[Matrix.MTRANS_X], this.matrixValuesBuffer[Matrix.MTRANS_Y] };
	}

	// ... /GET

	// ... ON

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean retVal = this.gestureDetector.onTouchEvent(event);
		retVal = this.scaleGestureDetector.onTouchEvent(event) || retVal;
		return retVal || super.onTouchEvent(event);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		float[] transform = getMatrixTransform();
		float[] scale = getMatrixScale();
		canvas.translate(transform[0], transform[1]);
		canvas.scale(scale[0], scale[1]);

		// drawEdgeEffectsUnclipped(canvas);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		Log.d(TAG, "OnLayout: " + l + ", " + t + ", " + r + ", " + b + ": " + getChildCount());
		for (int i = 0; i < getChildCount(); i++) {
			getChildAt(i).layout(l, t, r, b);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(widthMeasureSpec + 200, heightMeasureSpec + 100);
		Log.d(TAG, "OnMeasure: " + getMeasuredWidth() + ", " + getMeasuredHeight());
	}

	// ... /ON

	// ... GET

	public PointF getRelativePoint(float x, float y) {
		float[] transform = getMatrixTransform();
		float[] scale = getMatrixScale();
		return new PointF((x - transform[0]) / scale[0], (y - transform[1]) / scale[1]);
	}

	// ... /GET

	private void drawEdgeEffectsUnclipped(Canvas canvas) {
		boolean needsInvalidate = false;

		Matrix matrixTemp = canvas.getMatrix();
		canvas.save();
		Matrix matrixTemp2 = new Matrix();
		canvas.setMatrix(matrixTemp2);
		// canvas.setMatrix(this.matrixOriginal);

		if (!edgeEffectTop.isFinished()) {
			final int restoreCount = canvas.save();
			edgeEffectTop.setSize(getWidth(), getHeight());
			if (edgeEffectTop.draw(canvas)) {
				needsInvalidate = true;
			}
			canvas.restoreToCount(restoreCount);
		}

		if (!edgeEffectBottom.isFinished()) {
			final int restoreCount = canvas.save();
			canvas.translate(2 * getLeft() - getRight(), getBottom());
			canvas.rotate(180, getWidth(), 0);
			edgeEffectBottom.setSize(getWidth(), getHeight());
			if (edgeEffectBottom.draw(canvas)) {
				needsInvalidate = true;
			}
			canvas.restoreToCount(restoreCount);
		}

		if (!edgeEffectLeft.isFinished()) {
			final int restoreCount = canvas.save();
			canvas.translate(getLeft(), getBottom());
			canvas.rotate(-90, 0, 0);
			edgeEffectLeft.setSize(getHeight(), getWidth());
			if (edgeEffectLeft.draw(canvas)) {
				needsInvalidate = true;
			}
			canvas.restoreToCount(restoreCount);
		}

		if (!edgeEffectRight.isFinished()) {
			final int restoreCount = canvas.save();
			canvas.translate(getRight(), getTop());
			canvas.rotate(90, 0, 0);
			edgeEffectRight.setSize(getHeight(), getWidth());
			if (edgeEffectRight.draw(canvas)) {
				needsInvalidate = true;
			}
			canvas.restoreToCount(restoreCount);
		}

		canvas.restore();
		// canvas.setMatrix(this.matrix);
		canvas.setMatrix(matrixTemp);

		if (needsInvalidate)
			ViewCompat.postInvalidateOnAnimation(this);
	}

	@Override
	public void computeScroll() {
		super.computeScroll();

		boolean invalidate = false;

		if (this.scroller.computeScrollOffset()) {
			float[] transform = getMatrixTransform();
			float[] scale = getMatrixScale();
			float postX = (this.scroller.getCurrX() - transform[0]); // *scale[0];
			float postY = (this.scroller.getCurrY() - transform[1]); // *scale[0];
			this.matrix.postTranslate(postX, postY);
			invalidate = true;
		}

		if (this.zoomer.computeZoom()) {
			this.matrix.postScale(1 + this.zoomer.getCurrZoom(), 1 + this.zoomer.getCurrZoom(),
					this.zoomer.getFocalPoint().x, this.zoomer.getFocalPoint().y);
			invalidate = true;
		}

		if (invalidate)
			ViewCompat.postInvalidateOnAnimation(this);
	}

	// ... DO

	private void doScroll(float x, float y) {
		this.scroller.forceFinished(true);
		this.zoomer.forceFinished(true);

		float[] transform = this.getMatrixTransform();
		float[] scale = this.getMatrixScale();
		float newX = (transform[0] + x) * scale[0];
		float newY = (transform[1] + y) * scale[0];
		float maxX = scale[0] * getMeasuredWidth();
		float maxY = scale[0] * getMeasuredHeight();
		boolean restrained = false;

		if (!(newX <= maxX)) {
			edgeEffectLeft.onAbsorb((int) Math.abs(newX - maxX));
		}
		if (!(newX >= -maxX)) {
			edgeEffectRight.onAbsorb((int) Math.abs(newX - maxX));
		}
		if (!(newY <= maxY)) {
			edgeEffectTop.onAbsorb((int) Math.abs(newY - maxY));
		}
		if (!(newY >= -maxY)) {
			edgeEffectBottom.onAbsorb((int) Math.abs(newY - maxY));
		}

		this.matrix.postTranslate(newX <= maxX && newX >= -maxX ? x : 0, newY <= maxY && newY >= -maxY ? y : 0);
		if (restrained) {
			// this.matrix.setTranslate(newX, newY);
		} else {
			// this.matrix.postTranslate(x, y);
		}

		ViewCompat.postInvalidateOnAnimation(this);
	}

	private void doFling(int velocityX, int velocityY) {
		float[] transform = getMatrixTransform();
		float[] scale = getMatrixScale();

		this.scroller.forceFinished(true);
		this.zoomer.forceFinished(true);

		int maxX = (int) (getMeasuredWidth());
		int maxY = (int) (getMeasuredHeight());
		int minX = (int) (maxX * scale[0]);
		int minY = (int) (maxY * scale[1]);

		this.scroller.fling((int) transform[0], (int) transform[1], velocityX, velocityY, -minX, maxX, -minY, maxY);
		ViewCompat.postInvalidateOnAnimation(this);
	}

	public void doZoom(boolean zoomIn) {
		doZoom(zoomIn, new PointF(getMeasuredWidth() / 2, getMeasuredHeight() / 2));
	}

	public void doZoom(boolean zoomIn, PointF focalPoint) {
		this.scroller.forceFinished(true);
		this.zoomer.forceFinished(true);
		zoomer.startZoom(zoomIn ? ZOOM_AMOUNT : -ZOOM_AMOUNT);
		zoomer.setFocalPoint(focalPoint);
		ViewCompat.postInvalidateOnAnimation(this);
	}

	private void doScale(float scaleFactor, PointF focalPoint) {
		this.scroller.forceFinished(true);
		this.zoomer.forceFinished(true);
		this.matrix.postScale(scaleFactor, scaleFactor, focalPoint.x, focalPoint.y);
		ViewCompat.postInvalidateOnAnimation(this);
	}

	// ... /DO

	// CLASS

	public class MyScroller extends Scroller {

		public MyScroller(Context context) {
			super(context);
		}

		@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
		public float getCurrentVelocityCompat() {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				return getCurrVelocity();
			} else {
				return 0;
			}
		}

	}

	public class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			ViewCompat.postInvalidateOnAnimation(ViewportView.this);
			return true;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			scroller.forceFinished(true);
			ViewCompat.postInvalidateOnAnimation(ViewportView.this);
			return true;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			doZoom(true, new PointF(e.getX(), e.getY()));
			edgeEffectLeft.onAbsorb(10000);
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			doFling((int) (velocityX * 0.5), (int) (velocityY * 0.5));
			return true;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			doScroll(-distanceX, -distanceY);
			return true;
		}

	}

	public class MyScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			doScale(detector.getScaleFactor(), new PointF(detector.getFocusX(), detector.getFocusY()));
			return true;
		}

	}

	public class MyZoomer {

		private Interpolator mInterpolator;
		private int mAnimationDurationMillis;
		private boolean mFinished = true;
		public float mCurrentZoom;
		private long mStartRTC;
		private float mEndZoom;
		private PointF focalPoint = new PointF();

		public MyZoomer(Context context) {
			mInterpolator = new DecelerateInterpolator();
			mAnimationDurationMillis = context.getResources().getInteger(android.R.integer.config_shortAnimTime);
		}

		public void forceFinished(boolean finished) {
			mFinished = finished;
		}

		public void abortAnimation() {
			mFinished = true;
			mCurrentZoom = mEndZoom;
		}

		public void startZoom(float endZoom) {
			mStartRTC = SystemClock.elapsedRealtime();
			mEndZoom = endZoom;

			mFinished = false;
			mCurrentZoom = 1f;
		}

		public boolean computeZoom() {
			if (mFinished) {
				return false;
			}

			long tRTC = SystemClock.elapsedRealtime() - mStartRTC;
			if (tRTC >= mAnimationDurationMillis) {
				mFinished = true;
				mCurrentZoom = mEndZoom;
				return false;
			}

			float t = tRTC * 1f / mAnimationDurationMillis;
			mCurrentZoom = mEndZoom * mInterpolator.getInterpolation(t);
			return true;
		}

		public float getCurrZoom() {
			return mCurrentZoom;
		}

		public PointF getFocalPoint() {
			return focalPoint;
		}

		public void setFocalPoint(PointF focalPoint) {
			this.focalPoint = focalPoint;
		}
	}

	// /CLASS

}
