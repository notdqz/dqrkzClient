package com.dqrkz.module.modules;

import com.dqrkz.gui.setting.BoolSetting;
import com.dqrkz.gui.setting.SliderSetting;
import com.dqrkz.module.Category;
import com.dqrkz.module.Module;
import com.dqrkz.util.MoveUtil;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class NoSlow extends Module {
	public final BoolSetting cobweb = add(new BoolSetting("Cobweb", true));
	public final BoolSetting berryBush = add(new BoolSetting("Berry bush", true));
	public final BoolSetting powderSnow = add(new BoolSetting("Powder snow", true));
	public final BoolSetting honeySoulSand = add(new BoolSetting("Honey / soul sand", true));
	public final BoolSetting water = add(new BoolSetting("Water", true));
	public final BoolSetting lava = add(new BoolSetting("Lava", true));
	public final SliderSetting fluidSpeed = add(new SliderSetting("Fluid speed", 1.0f, 0.5f, 1.5f, 0.05f, "x"));

	public NoSlow() {
		super("No Slow", Category.MOVEMENT);
	}

	// Called from EntityMixin
	public boolean blocksStuck(BlockState state) {
		if (!isEnabled()) return false;
		if (state.is(Blocks.COBWEB)) return cobweb.value;
		if (state.is(Blocks.SWEET_BERRY_BUSH)) return berryBush.value;
		if (state.is(Blocks.POWDER_SNOW)) return powderSnow.value;
		return false;
	}

	// Called from EntityMixin
	public boolean blocksSpeedFactor() {
		return isEnabled() && honeySoulSand.value;
	}

	// Fluids: replace the drag-reduced horizontal velocity with ground-like speed.
	@Override
	public void onEndTick() {
		LocalPlayer p = mc().player;
		if (p == null || p.isPassenger() || p.getAbilities().flying) return;

		boolean inFluid = (water.value && p.isInWater()) || (lava.value && p.isInLava());
		if (!inFluid) return;

		Vec3 dir = MoveUtil.inputDirection(p);
		if (dir == null) return;

		double speed = (p.isSprinting() ? 0.28 : 0.215) * fluidSpeed.value;
		if (p.isShiftKeyDown()) speed *= 0.3;

		Vec3 v = p.getDeltaMovement();
		p.setDeltaMovement(dir.x * speed, v.y, dir.z * speed);
	}
}