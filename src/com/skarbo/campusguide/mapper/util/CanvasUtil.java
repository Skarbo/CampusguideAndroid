package com.skarbo.campusguide.mapper.util;

import java.util.List;

import android.graphics.PointF;
import android.graphics.RectF;

public class CanvasUtil {

	public static boolean polygonContains(List<PointF> coordinates, PointF point) {
		boolean oddTransitions = false;
		for (int i = 0, j = coordinates.size() - 1; i < coordinates.size(); j = i++) {
			if ((coordinates.get(i).y < point.y && coordinates.get(j).y >= point.y)
					|| (coordinates.get(j).y < point.y && coordinates.get(i).y >= point.y)) {
				// if( ( polyY[ i ] < y && polyY[ j ] >= y ) || ( polyY[ j ] < y
				// && polyY[ i ] >= y ) )
				if (coordinates.get(i).x + (point.y - coordinates.get(i).y)
						/ (coordinates.get(j).y - coordinates.get(i).y) * (coordinates.get(j).x - coordinates.get(i).x) < point.x) {
					// if( polyX[ i ] + ( y - polyY[ i ] ) / ( polyY[ j ] -
					// polyY[ i ] ) * ( polyX[ j ] - polyX[ i ] ) < x )
					oddTransitions = !oddTransitions;
				}
			}
		}
		return oddTransitions;
	}

	public static boolean polygonContains(float[] polyX, float[] polyY, int polySides, float x, float y) {
		boolean c = false;
		int i, j = 0;
		for (i = 0, j = polySides - 1; i < polySides; j = i++) {
			if (((polyY[i] > y) != (polyY[j] > y))
					&& (x < (polyX[j] - polyX[i]) * (y - polyY[i]) / (polyY[j] - polyY[i]) + polyX[i]))
				c = !c;
		}
		return c;
	}

	public static RectF getBounds(List<PointF> coordinates) {
		RectF bound = new RectF(0, 0, 0, 0);

		if (coordinates == null || coordinates.isEmpty())
			return bound;

		bound.left = coordinates.get(0).x;
		bound.right = coordinates.get(0).x;
		bound.top = coordinates.get(0).y;
		bound.bottom = coordinates.get(0).y;

		for (PointF point : coordinates) {
			if (point.x < bound.left)
				bound.left = point.x;
			if (point.x > bound.right)
				bound.right = point.x;
			if (point.y < bound.top)
				bound.top = point.y;
			if (point.y > bound.bottom)
				bound.bottom = point.y;
		}

		return bound;

	}

	public static RectF getBounds(RectF[] bounds) {
		RectF bound = new RectF(0, 0, 0, 0);

		if (bounds == null || bounds.length == 0)
			return bound;

		bound.left = bounds[0].left;
		bound.right = bounds[0].right;
		bound.top = bounds[0].top;
		bound.bottom = bounds[0].bottom;

		for (RectF rect : bounds) {
			if (rect.left < bound.left)
				bound.left = rect.left;
			if (rect.right > bound.right)
				bound.right = rect.right;
			if (rect.top < bound.top)
				bound.top = rect.top;
			if (rect.bottom > bound.bottom)
				bound.bottom = rect.bottom;
		}

		return bound;
	}

}
