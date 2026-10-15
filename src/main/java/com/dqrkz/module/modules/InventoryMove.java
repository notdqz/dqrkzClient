package com.dqrkz.module.modules;

import com.dqrkz.gui.MenuScreen;
import com.dqrkz.gui.setting.BoolSetting;
import com.dqrkz.module.Category;
import com.dqrkz.module.Module;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

public class InventoryMove extends Module {
	private final BoolSetting containers = add(new BoolSetting("Containers", true));
	private final BoolSetting ownMenu = add(new BoolSetting("dqrkz menu", true));

	public InventoryMove() {
		super("Inventory Move", Category.MOVEMENT);
	}

	// Must run before other modules so they can override the key states.
	@Override
	public int priority() {
		return -100;
	}

	@Override
	public void onTick() {
		Screen screen = mc().screen;
		if (screen == null) return;

		boolean allowed = (ownMenu.value && screen instanceof MenuScreen)
				|| (containers.value && screen instanceof AbstractContainerScreen<?>);
		if (!allowed) return;

		// Never steal keys while typing (anvil rename, creative search...)
		if (screen.getFocused() instanceof EditBox) return;

		// Re-syncs all keyboard-bound keys with the physical keyboard state.
		KeyMapping.setAll();
	}
}