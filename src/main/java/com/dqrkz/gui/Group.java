package com.dqrkz.gui;

import com.dqrkz.gui.setting.Setting;

import java.util.ArrayList;
import java.util.List;

public class Group {
	public final String title;
	public final int column; // 0 = left, 1 = right
	public final List<Setting> settings = new ArrayList<>();

	public Group(String title, int column) {
		this.title = title;
		this.column = column;
	}

	public Group add(Setting setting) {
		settings.add(setting);
		return this;
	}
}
