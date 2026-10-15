package com.dqrkz.module;

import com.dqrkz.module.modules.AutoSprint;
import com.dqrkz.module.modules.Bhop;
import com.dqrkz.module.modules.InventoryMove;
import com.dqrkz.module.modules.NoSlow;
import com.dqrkz.module.modules.SafeWalk;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import java.util.Comparator;
import java.util.List;

public final class ModuleManager {
	private ModuleManager() {}

	public static final AutoSprint AUTO_SPRINT = new AutoSprint();
	public static final SafeWalk SAFE_WALK = new SafeWalk();
	public static final InventoryMove INVENTORY_MOVE = new InventoryMove();
	public static final NoSlow NO_SLOW = new NoSlow();
	public static final Bhop BHOP = new Bhop();

	/** Menu order. */
	public static final List<Module> ALL = List.of(
			AUTO_SPRINT, SAFE_WALK, INVENTORY_MOVE, NO_SLOW, BHOP
	);

	/** Tick order (by priority). */
	private static final List<Module> TICK_ORDER = ALL.stream()
			.sorted(Comparator.comparingInt(Module::priority))
			.toList();

	public static void init() {
		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			if (client.player == null) return;
			for (Module m : TICK_ORDER) m.startTick();
		});
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player == null) return;
			for (Module m : TICK_ORDER) m.endTick();
		});
	}
}