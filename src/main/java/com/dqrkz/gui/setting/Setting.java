package com.dqrkz.gui.setting;

public abstract class Setting {
	public final String name;
	// Position is assigned by the screen every frame, used for click detection.
	public int x, y, w;

	protected Setting(String name) {
		this.name = name;
	}

	public abstract int height();

	public boolean hit(double mx, double my) {
		return mx >= x && mx < x + w && my >= y && my < y + height();
	}
}
