package com.dqrkz.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;

public final class MoveUtil {
	private MoveUtil() {}

	/** World-space direction of the currently held WASD keys, or null if none. */
	public static Vec3 inputDirection(LocalPlayer p) {
		Options o = Minecraft.getInstance().options;
		double f = (o.keyUp.isDown() ? 1 : 0) - (o.keyDown.isDown() ? 1 : 0);
		double s = (o.keyLeft.isDown() ? 1 : 0) - (o.keyRight.isDown() ? 1 : 0);
		if (f == 0 && s == 0) return null;

		double len = Math.sqrt(f * f + s * s);
		f /= len;
		s /= len;

		double yaw = Math.toRadians(p.getYRot());
		double sin = Math.sin(yaw), cos = Math.cos(yaw);
		return new Vec3(s * cos - f * sin, 0, f * cos + s * sin);
	}
}