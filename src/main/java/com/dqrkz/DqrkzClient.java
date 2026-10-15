package com.dqrkz;

import com.dqrkz.gui.MenuScreen;
import com.dqrkz.module.ModuleManager;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class DqrkzClient implements ClientModInitializer {
	private static KeyMapping openMenu;

	@Override
	public void onInitializeClient() {
		KeyMapping.Category category = KeyMapping.Category.register(Dqrkz.id("main"));

		openMenu = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.dqrkz.menu",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_RIGHT_SHIFT,
				category
		));

		ModuleManager.init();

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (openMenu.consumeClick()) {
				if (client.screen == null) {
					client.setScreen(new MenuScreen());
				}
			}
		});
	}
}