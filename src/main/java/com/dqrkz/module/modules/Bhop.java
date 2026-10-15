package com.dqrkz.module.modules;

import com.dqrkz.gui.setting.BoolSetting;
import com.dqrkz.module.Category;
import com.dqrkz.module.Module;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;

public class Bhop extends Module {
	private final BoolSetting sprintOnly = add(new BoolSetting("Sprint only", true));
	private boolean forced;

	public Bhop() {
		super("Bhop", Category.MOVEMENT);
	}

	@Override
	public void onTick() {
		LocalPlayer p = mc().player;
		Options o = mc().options;
		if (p == null) return;

		if (o.keyUp.isDown() && canHop(p)) {
			// Jump key is only down on ground ticks. Releasing it in the air resets
			// vanilla's jump delay, so we jump on the first tick we land.
			o.keyJump.setDown(p.onGround());
			forced = true;
		} else if (forced) {
			o.keyJump.setDown(false);
			forced = false;
		}
	}

	private boolean canHop(LocalPlayer p) {
		return !p.isInWater()
				&& !p.isInLava()
				&& !p.onClimbable()
				&& !p.isPassenger()
				&& !p.isShiftKeyDown()
				&& !p.getAbilities().flying
				&& (!sprintOnly.value || p.isSprinting());
	}

	@Override
	public void onDisable() {
		if (forced) {
			mc().options.keyJump.setDown(false);
			forced = false;
		}
	}
}