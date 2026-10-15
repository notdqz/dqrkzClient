package com.dqrkz.mixin;

import com.dqrkz.module.ModuleManager;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin {
	// Same check vanilla uses for sneaking at block edges.
	@Inject(method = "isStayingOnGroundSurface", at = @At("HEAD"), cancellable = true)
	private void dqrkz$safeWalk(CallbackInfoReturnable<Boolean> cir) {
		if ((Object) this instanceof LocalPlayer && ModuleManager.SAFE_WALK.isEnabled()) {
			cir.setReturnValue(true);
		}
	}
}