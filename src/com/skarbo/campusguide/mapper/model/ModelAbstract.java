package com.skarbo.campusguide.mapper.model;

public abstract class ModelAbstract implements Model {

	protected int id;
	protected int updated;
	protected int registered;

	public int getId() {
		return id;
	}

	public int getUpdated() {
		return updated > 0 ? updated : getRegistered();
	}

	public int getRegistered() {
		return registered;
	}

	public boolean isUpdated(Model model) {
		return getUpdated() > model.getUpdated();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ModelAbstract) {
			return ((ModelAbstract) o).getId() == getId();
		}
		return super.equals(o);
	}

}
