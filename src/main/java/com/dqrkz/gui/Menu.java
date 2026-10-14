package com.dqrkz.gui;

import com.dqrkz.gui.setting.BoolSetting;
import com.dqrkz.gui.setting.SliderSetting;

import java.util.List;

public final class Menu {
	private Menu() {}

	public static final List<Tab> TABS = List.of(
			new Tab("Combat")
					.add(new Group("General", 0)
							.add(new BoolSetting("Enabled", false))
							.add(new SliderSetting("Value", 50, 0, 100, 1, "%"))
							.add(new BoolSetting("Option", true)))
					.add(new Group("Extra", 1)
							.add(new BoolSetting("Enabled", false))
							.add(new SliderSetting("Range", 3.0f, 0, 6, 0.1f, ""))),
			new Tab("Movement")
					.add(new Group("General", 0)
							.add(new BoolSetting("Enabled", false))
							.add(new SliderSetting("Speed", 1.0f, 0.5f, 3, 0.1f, "x"))),
			new Tab("Visuals")
					.add(new Group("General", 0)
							.add(new BoolSetting("Enabled", false))
							.add(new BoolSetting("Option", false)))
					.add(new Group("Colors", 1)
							.add(new SliderSetting("Opacity", 80, 0, 100, 1, "%"))),
			new Tab("Misc")
					.add(new Group("General", 0)
							.add(new BoolSetting("Enabled", false))),
			new Tab("Config")
					.add(new Group("Menu", 0)
							.add(new BoolSetting("Placeholder", false)))
	);
}
