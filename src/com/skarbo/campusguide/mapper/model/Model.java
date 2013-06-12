package com.skarbo.campusguide.mapper.model;

public interface Model {

	public int getId();

	public int getForeignId();

	public int getUpdated();

	/**
	 * @param model
	 * @return True if current Model is more updated then given Model
	 */
	public boolean isUpdated(Model model);

}
