package com.skarbo.campusguide.mapper.util;

public class PolygonShape {
	public PaintPath border;
	public PaintPath fill;

	public PolygonShape() {
		this.border = new PaintPath();
		this.fill = new PaintPath();
	}
}