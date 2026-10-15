package com.dqrkz.module;

import com.dqrkz.module.modules.AutoEat;
import com.dqrkz.module.modules.AutoSprint;
import com.dqrkz.module.modules.AutoTool;
import com.dqrkz.module.modules.AutoTotem;
import com.dqrkz.module.modules.Bhop;
import com.dqrkz.module.modules.FastPlace;
import com.dqrkz.module.modules.Fullbright;
import com.dqrkz.module.modules.InventoryMove;
import com.dqrkz.module.modules.NoSlow;
import com.dqrkz.module.modules.SafeWalk;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import java.util.Comparator;
import java.util.List;

public final class ModuleManager {
	private ModuleManager() {}

	// Movement
	public static final AutoSprint AUTO_SPRINT = new AutoSprint();
	public static final SafeWalk SAFE_WALK = new SafeWalk();
	public static final InventoryMove INVENTORY_MOVE = new InventoryMove();
	public static final NoSlow NO_SLOW = new NoSlow();
	public static final Bhop BHOP = new Bhop();

	// Player
	public static final FastPlace FAST_PLACE = new FastPlace();
	public static final AutoTool AUTO_TOOL = new AutoTool();
	public static final AutoEat AUTO_EAT = new AutoEat();
	public static final AutoTotem AUTO_TOTEM = new AutoTotem();

	// Visuals
	public static final Fullbright FULLBRIGHT = new Fullbright();

	/** Menu order. */
	public static final List<Module> ALL = List.of(
			AUTO_SPRINT, SAFE_WALK, INVENTORY_MOVE, NO_SLOW, BHOP,
			FAST_PLACE, AUTO_TOOL, AUTO_EAT, AUTO_TOTEM,
			FULLBRIGHT
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