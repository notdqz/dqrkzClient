package com.dqrkz.module.modules;

import com.dqrkz.gui.setting.BoolSetting;
import com.dqrkz.gui.setting.SliderSetting;
import com.dqrkz.module.Category;
import com.dqrkz.module.Module;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class AutoEat extends Module {
	private final SliderSetting hunger = add(new SliderSetting("Eat at hunger", 14, 1, 19, 1, ""));
	private final SliderSetting health = add(new SliderSetting("Eat at health", 8, 1, 20, 1, ""));
	private final SliderSetting delay = add(new SliderSetting("Delay", 2, 0, 10, 1, "t"));
	private final BoolSetting switchBack = add(new BoolSetting("Switch back", true));
	private final BoolSetting gapples = add(new BoolSetting("Golden apples", false));

	private enum State { IDLE, SWAP_WAIT, EATING, RESTORE_WAIT }

	private State state = State.IDLE;
	private int timer;
	private int cooldown;
	private boolean started;
	private int prevSlot = -1;
	private int eatSlot = -1;

	public AutoEat() {
		super("Auto Eat", Category.PLAYER);
	}

	@Override
	public void onTick() {
		LocalPlayer p = mc().player;
		if (p == null) return;

		// Opening any screen mid-sequence cancels it.
		if (state != State.IDLE && mc().screen != null) {
			abort(p);
			return;
		}

		switch (state) {
			case IDLE -> {
				if (cooldown > 0) { cooldown--; return; }
				if (mc().screen != null || p.isUsingItem() || p.getAbilities().invulnerable) return;

				int slot = pickFood(p);
				if (slot < 0) return;

				prevSlot = p.getInventory().getSelectedSlot();
				eatSlot = slot;
				p.getInventory().setSelectedSlot(slot);
				timer = (int) delay.value;
				state = State.SWAP_WAIT;
			}
			case SWAP_WAIT -> {
				if (--timer > 0) return;
				// Use the held item directly (like right-clicking air), so we never
				// accidentally open a chest / crafting table we're looking at.
				mc().gameMode.useItem(p, InteractionHand.MAIN_HAND);
				mc().options.keyUse.setDown(true);
				started = false;
				timer = 0;
				state = State.EATING;
			}
			case EATING -> {
				mc().options.keyUse.setDown(true); // vanilla cancels eating if the key isn't held
				timer++;
				if (p.isUsingItem()) {
					started = true;
				} else if (started || timer > 10) {
					mc().options.keyUse.setDown(false);
					if (!started) cooldown = 20; // never started: don't retry instantly
					timer = (int) delay.value;
					state = State.RESTORE_WAIT;
				}
			}
			case RESTORE_WAIT -> {
				if (--timer > 0) return;
				restore(p);
				state = State.IDLE;
			}
		}
	}

	private int pickFood(LocalPlayer p) {
		boolean hungry = p.getFoodData().getFoodLevel() <= (int) hunger.value;
		boolean hurt = p.getHealth() <= health.value;
		if (!hungry && !hurt) return -1;

		boolean canEat = p.canEat(false);
		Inventory inv = p.getInventory();
		int best = -1;
		float bestScore = -1;

		for (int i = 0; i < 9; i++) {
			ItemStack s = inv.getItem(i);
			FoodProperties f = s.get(DataComponents.FOOD);
			if (f == null || isBad(s)) continue;

			boolean gapple = s.is(Items.GOLDEN_APPLE) || s.is(Items.ENCHANTED_GOLDEN_APPLE);
			if (gapple && !gapples.value) continue;
			if (!canEat && !f.canAlwaysEat()) continue;

			float score = f.nutrition() + f.saturation();
			if (hurt && gapple) score += 100;
			if (score > bestScore) {
				bestScore = score;
				best = i;
			}
		}
		return best;
	}

	private boolean isBad(ItemStack s) {
		return s.is(Items.ROTTEN_FLESH) || s.is(Items.SPIDER_EYE) || s.is(Items.PUFFERFISH)
				|| s.is(Items.POISONOUS_POTATO) || s.is(Items.CHICKEN) || s.is(Items.CHORUS_FRUIT)
				|| s.is(Items.SUSPICIOUS_STEW);
	}

	private void restore(LocalPlayer p) {
		if (switchBack.value && prevSlot >= 0 && p.getInventory().getSelectedSlot() == eatSlot) {
			p.getInventory().setSelectedSlot(prevSlot);
		}
		prevSlot = -1;
	}

	private void abort(LocalPlayer p) {
		mc().options.keyUse.setDown(false);
		restore(p);
		state = State.IDLE;
		cooldown = 20;
	}

	@Override
	public void onDisable() {
		LocalPlayer p = mc().player;
		if (state != State.IDLE && p != null) abort(p);
		state = State.IDLE;
	}
}