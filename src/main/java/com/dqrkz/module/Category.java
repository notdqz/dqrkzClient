package com.dqrkz.module;

public enum Category {
	COMBAT("Combat"),
	MOVEMENT("Movement"),
	PLAYER("Player"),
	VISUALS("Visuals");

	public final String label;

	Category(String label) {
		this.label = label;
	}
}