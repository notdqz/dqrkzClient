package com.dqrkz.gui;

import java.util.ArrayList;
import java.util.List;

public class Tab {
	public final String name;
	public final List<Group> groups = new ArrayList<>();

	public Tab(String name) {
		this.name = name;
	}

	public Tab add(Group group) {
		groups.add(group);
		return this;
	}
}
