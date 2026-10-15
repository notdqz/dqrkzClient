package com.dqrkz.gui;

import com.dqrkz.gui.setting.Setting;
import com.dqrkz.module.Category;
import com.dqrkz.module.Module;
import com.dqrkz.module.ModuleManager;

import java.util.ArrayList;
import java.util.List;

public final class Menu {
	private Menu() {}

	public static final List<Tab> TABS = build();

	private static List<Tab> build() {
		List<Tab> tabs = new ArrayList<>();

		for (Category category : Category.values()) {
			Tab tab = new Tab(category.label);
			int[] heights = {0, 0};

			for (Module module : ModuleManager.ALL) {
				if (module.category != category) continue;

				int h = 0;
				for (Setting s : module.settings()) h += s.height();

				int col = heights[0] <= heights[1] ? 0 : 1;
				heights[col] += h + 28; // group chrome + gap

				Group group = new Group(module.name, col);
				for (Setting s : module.settings()) group.add(s);
				tab.add(group);
			}
			tabs.add(tab);
		}
		return List.copyOf(tabs);
	}
}