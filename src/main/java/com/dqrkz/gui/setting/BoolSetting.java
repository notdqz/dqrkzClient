package com.dqrkz.gui.setting;

public class BoolSetting extends Setting {
	public boolean value;

	public BoolSetting(String name, boolean value) {
		super(name);
		this.value = value;
	}

	@Override
	public int height() {
		return 12;
	}
}
