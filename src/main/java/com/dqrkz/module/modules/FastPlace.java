package com.dqrkz.module.modules;

import com.dqrkz.gui.setting.BoolSetting;
import com.dqrkz.gui.setting.SliderSetting;
import com.dqrkz.mixin.MinecraftAccessor;
import com.dqrkz.module.Category;
import com.dqrkz.module.Module;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.BlockItem;

public class FastPlace extends Module {
	private final SliderSetting delay = add(new SliderSetting("Delay", 0, 0, 4, 1, "t"));
	private final BoolSetting blocksOnly = add(new BoolSetting("Blocks only", true));

	public FastPlace() {
		super("Fast Place", Category.PLAYER);
	}

	@Override
	public void onTick() {
		LocalPlayer p = mc().player;
		if (p == null) return;
		if (blocksOnly.value && !(p.getMainHandItem().getItem() instanceof BlockItem)) return;

		MinecraftAccessor acc = (MinecraftAccessor) (Object) mc();
		int target = (int) delay.value;
		if (acc.dqrkz$getRightClickDelay() > target) {
			acc.dqrkz$setRightClickDelay(target);
		}
	}
}