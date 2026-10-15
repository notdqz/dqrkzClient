package com.dqrkz.module.modules;

import com.dqrkz.module.Category;
import com.dqrkz.module.Module;
import net.minecraft.client.Options;

public class AutoSprint extends Module {
	private boolean forced;

	public AutoSprint() {
		super("Auto Sprint", Category.MOVEMENT);
	}

	@Override
	public void onTick() {
		Options o = mc().options;
		boolean forward = o.keyUp.isDown();

		if (forward && !o.keySprint.isDown()) {
			o.keySprint.setDown(true);
			forced = true;
		} else if (!forward && forced) {
			o.keySprint.setDown(false);
			forced = false;
		}
	}

	@Override
	public void onDisable() {
		if (forced) {
			mc().options.keySprint.setDown(false);
			forced = false;
		}
	}
}