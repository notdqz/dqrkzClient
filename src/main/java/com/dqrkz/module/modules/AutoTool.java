package com.dqrkz.module.modules;

import com.dqrkz.gui.setting.BoolSetting;
import com.dqrkz.gui.setting.SliderSetting;
import com.dqrkz.module.Category;
import com.dqrkz.module.Module;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class AutoTool extends Module {
	private final SliderSetting delay = add(new SliderSetting("Delay", 2, 0, 8, 1, "t"));
	private final BoolSetting switchBack = add(new BoolSetting("Switch back", true));

	private int mineTicks;
	private int returnTicks;
	private int prevSlot = -1;
	private int toolSlot = -1;
	private BlockPos lastPos;

	public AutoTool() {
		super("Auto Tool", Category.PLAYER);
	}

	@Override
	public void onTick() {
		LocalPlayer p = mc().player;
		if (p == null || mc().level == null) return;

		HitResult hr = mc().hitResult;
		BlockHitResult hit = (hr instanceof BlockHitResult b && b.getType() == HitResult.Type.BLOCK) ? b : null;
		boolean mining = hit != null && mc().screen == null && mc().options.keyAttack.isDown();
		Inventory inv = p.getInventory();

		if (mining) {
			returnTicks = 0;
			BlockPos pos = hit.getBlockPos();
			if (!pos.equals(lastPos)) {
				lastPos = pos;
				mineTicks = 0;
			}
			mineTicks++;

			if (mineTicks > (int) delay.value) {
				BlockState state = mc().level.getBlockState(pos);
				int cur = inv.getSelectedSlot();
				int best = bestSlot(inv, state);
				if (best >= 0 && best != cur) {
					if (prevSlot < 0) prevSlot = cur;
					inv.setSelectedSlot(best);
					toolSlot = best;
				}
			}
		} else {
			mineTicks = 0;
			lastPos = null;

			if (prevSlot >= 0) {
				// You scrolled somewhere else yourself -> leave it alone.
				if (!switchBack.value || inv.getSelectedSlot() != toolSlot) {
					prevSlot = -1;
				} else if (++returnTicks > (int) delay.value + 2) {
					inv.setSelectedSlot(prevSlot);
					prevSlot = -1;
					returnTicks = 0;
				}
			}
		}
	}

	/** Fastest hotbar slot for this block, or -1 if the held item is already best. */
	private int bestSlot(Inventory inv, BlockState state) {
		int cur = inv.getSelectedSlot();
		float bestSpeed = inv.getItem(cur).getDestroySpeed(state);
		int best = -1;

		for (int i = 0; i < 9; i++) {
			if (i == cur) continue;
			float speed = inv.getItem(i).getDestroySpeed(state);
			if (speed > bestSpeed + 0.01f) {
				bestSpeed = speed;
				best = i;
			}
		}
		return best;
	}

	@Override
	public void onDisable() {
		prevSlot = -1;
		mineTicks = 0;
	}
}