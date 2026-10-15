package com.dqrkz.module.modules;

import com.dqrkz.gui.setting.BoolSetting;
import com.dqrkz.gui.setting.SliderSetting;
import com.dqrkz.module.Category;
import com.dqrkz.module.Module;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Items;

public class AutoTotem extends Module {
	private final SliderSetting delay = add(new SliderSetting("Delay", 0, 0, 10, 1, "t"));
	private final BoolSetting onlyAfterPop = add(new BoolSetting("Only after pop", true));
	private final BoolSetting searchInventory = add(new BoolSetting("Search inventory", true));

	private boolean hadTotem;
	private boolean pending;
	private int timer;
	private int cooldown;

	public AutoTotem() {
		super("Auto Totem", Category.PLAYER);
	}

	@Override
	public void onTick() {
		LocalPlayer p = mc().player;
		if (p == null || p.isDeadOrDying()) return;

		boolean has = p.getOffhandItem().is(Items.TOTEM_OF_UNDYING);

		if (has) {
			pending = false;
		} else {
			// A totem left the offhand (popped), or we're in "always fill" mode.
			if (!pending && (hadTotem || !onlyAfterPop.value)) {
				pending = true;
				timer = (int) delay.value;
				cooldown = 0;
			}

			if (pending) {
				if (cooldown > 0) {
					cooldown--;
				} else if (timer-- <= 0) {
					// Container clicks on the inventory menu only work while no other menu is open.
					if (p.containerMenu == p.inventoryMenu && swapIn(p)) {
						cooldown = 4; // give the server time to answer before re-checking
					}
				}
			}
		}
		hadTotem = has;
	}

	private boolean swapIn(LocalPlayer p) {
		Inventory inv = p.getInventory();
		int found = -1;

		for (int i = 0; i < 9 && found < 0; i++) {
			if (inv.getItem(i).is(Items.TOTEM_OF_UNDYING)) found = i;
		}
		if (found < 0 && searchInventory.value) {
			for (int i = 9; i < 36 && found < 0; i++) {
				if (inv.getItem(i).is(Items.TOTEM_OF_UNDYING)) found = i;
			}
		}
		if (found < 0) return false;

		// Inventory index -> InventoryMenu slot id (hotbar is 36-44, main is 9-35)
		int menuSlot = found < 9 ? 36 + found : found;

		// Button 40 = swap with offhand, same as pressing F over a slot.
		mc().gameMode.handleInventoryMouseClick(p.inventoryMenu.containerId, menuSlot, 40, ClickType.SWAP, p);
		return true;
	}

	@Override
	public void onDisable() {
		pending = false;
		hadTotem = false;
	}
}