package com.dqrkz.mixin;

import com.dqrkz.module.ModuleManager;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
	// Cobweb, berry bush, powder snow
	@Inject(method = "makeStuckInBlock", at = @At("HEAD"), cancellable = true)
	private void dqrkz$noStuck(BlockState state, Vec3 multiplier, CallbackInfo ci) {
		if ((Object) this instanceof LocalPlayer && ModuleManager.NO_SLOW.blocksStuck(state)) {
			ci.cancel();
		}
	}

	// Honey block, soul sand
	@Inject(method = "getBlockSpeedFactor", at = @At("HEAD"), cancellable = true)
	private void dqrkz$noBlockSlow(CallbackInfoReturnable<Float> cir) {
		if ((Object) this instanceof LocalPlayer && ModuleManager.NO_SLOW.blocksSpeedFactor()) {
			cir.setReturnValue(1.0F);
		}
	}
}