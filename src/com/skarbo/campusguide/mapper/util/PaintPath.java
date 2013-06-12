package com.skarbo.campusguide.mapper.util;

import android.graphics.Paint;
import android.graphics.Path;

public class PaintPath {
	public Paint paint;
	public Path path;

	public PaintPath() {
		this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		this.path = new Path();
	}
}