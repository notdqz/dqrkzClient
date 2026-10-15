package com.dqrkz.module.modules;

import com.dqrkz.module.Category;
import com.dqrkz.module.Module;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/** Client-side-only invisible night vision. The server never knows about it. */
public class Fullbright extends Module {
	private boolean granted;

	public Fullbright() {
		super("Fullbright", Category.VISUALS);
	}

	@Override
	public void onTick() {
		LocalPlayer p = mc().player;
		if (p == null) return;

		if (!p.hasEffect(MobEffects.NIGHT_VISION)) {
			p.addEffect(new MobEffectInstance(
					MobEffects.NIGHT_VISION,
					MobEffectInstance.INFINITE_DURATION,
					0, false, false, false
			));
			granted = true;
		}
	}

	@Override
	public void onDisable() {
		LocalPlayer p = mc().player;
		if (granted && p != null) {
			p.removeEffect(MobEffects.NIGHT_VISION);
		}
		granted = false;
	}
}