package com.dqrkz.gui.setting;

import java.util.Locale;

public class SliderSetting extends Setting {
	public final float min, max, step;
	public final String suffix;
	public float value;

	public SliderSetting(String name, float value, float min, float max, float step, String suffix) {
		super(name);
		this.value = value;
		this.min = min;
		this.max = max;
		this.step = step;
		this.suffix = suffix;
	}

	@Override
	public int height() {
		return 20;
	}

	public float fraction() {
		return (value - min) / (max - min);
	}

	public void setFromMouse(double mouseX) {
		double t = Math.max(0, Math.min(1, (mouseX - x) / (double) w));
		double raw = min + t * (max - min);
		double snapped = Math.round((raw - min) / step) * step + min;
		value = (float) Math.max(min, Math.min(max, snapped));
	}

	public String display() {
		String num = step >= 1 ? String.valueOf(Math.round(value)) : String.format(Locale.ROOT, "%.1f", value);
		return num + suffix;
	}
}
