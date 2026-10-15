package com.dqrkz.module;

import com.dqrkz.gui.setting.BoolSetting;
import com.dqrkz.gui.setting.Setting;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {
	public final String name;
	public final Category category;
	public final BoolSetting enabled = new BoolSetting("Enabled", false);

	private final List<Setting> settings = new ArrayList<>();
	private boolean wasEnabled;

	protected Module(String name, Category category) {
		this.name = name;
		this.category = category;
		settings.add(enabled);
	}

	protected <T extends Setting> T add(T setting) {
		settings.add(setting);
		return setting;
	}

	public List<Setting> settings() {
		return settings;
	}

	public boolean isEnabled() {
		return enabled.value;
	}

	protected static Minecraft mc() {
		return Minecraft.getInstance();
	}

	/** Lower runs first each tick. */
	public int priority() {
		return 0;
	}

	public void onEnable() {}
	public void onDisable() {}
	public void onTick() {}     // start of client tick
	public void onEndTick() {}  // end of client tick

	final void startTick() {
		boolean on = enabled.value;
		if (on && !wasEnabled) onEnable();
		if (!on && wasEnabled) onDisable();
		wasEnabled = on;
		if (on) onTick();
	}

	final void endTick() {
		if (enabled.value) onEndTick();
	}
}