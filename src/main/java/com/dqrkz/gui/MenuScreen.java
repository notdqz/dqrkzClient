package com.dqrkz.gui;

import com.dqrkz.gui.setting.BoolSetting;
import com.dqrkz.gui.setting.Setting;
import com.dqrkz.gui.setting.SliderSetting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class MenuScreen extends Screen {
	private static final int W = 400;
	private static final int H = 250;
	private static final int TITLE_H = 16;
	private static final int FOOTER_H = 14;
	private static final int SIDEBAR_W = 80;
	private static final int TAB_H = 20;
	private static final int PAD = 8;
	private static final int GROUP_GAP = 12;
	private static final int GROUP_HEAD = 12;

	private static int winX = Integer.MIN_VALUE;
	private static int winY;
	private static int selectedTab = 0;
	private static int scroll = 0;

	private boolean draggingWindow;
	private int dragOffX, dragOffY;
	private SliderSetting activeSlider;

	public MenuScreen() {
		super(Component.literal("dqrkz"));
	}

	@Override
	protected void init() {
		if (winX == Integer.MIN_VALUE) {
			winX = (width - W) / 2;
			winY = (height - H) / 2;
		}
		clampWindow();
	}

	private void clampWindow() {
		winX = Math.max(0, Math.min(winX, width - W));
		winY = Math.max(0, Math.min(winY, height - H));
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float delta) {
		g.fill(0, 0, width, height, 0x55000000);
	}

	// ---------------------------------------------------------------- layout

	private static int groupHeight(Group group) {
		int h = GROUP_HEAD + 4;
		for (Setting s : group.settings) h += s.height();
		return h;
	}

	private static int contentHeight(Tab tab) {
		int[] col = {0, 0};
		for (Group g : tab.groups) col[g.column] += groupHeight(g) + GROUP_GAP;
		return PAD + 4 + Math.max(col[0], col[1]) - GROUP_GAP + PAD;
	}

	private static int viewHeight() {
		return H - TITLE_H - 1 - FOOTER_H;
	}

	private static int maxScroll() {
		return Math.max(0, contentHeight(Menu.TABS.get(selectedTab)) - viewHeight());
	}

	// ---------------------------------------------------------------- drawing

	private void text(GuiGraphics g, String s, int x, int y, int color) {
		g.drawString(font, s, x, y, color, false);
	}

	private void outline(GuiGraphics g, int x, int y, int w, int h, int c) {
		g.fill(x, y, x + w, y + 1, c);
		g.fill(x, y + h - 1, x + w, y + h, c);
		g.fill(x, y, x + 1, y + h, c);
		g.fill(x + w - 1, y, x + w, y + h, c);
	}

	@Override
	public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
		renderBackground(g, mouseX, mouseY, delta);

		int x = winX, y = winY;

		// Window
		g.fill(x - 1, y - 1, x + W + 1, y + H + 1, Theme.OUTER);
		g.fill(x, y, x + W, y + H, Theme.BG);

		// Title bar
		g.fill(x, y, x + W, y + TITLE_H, Theme.TITLE_BG);
		g.fill(x, y, x + W, y + 1, Theme.ACCENT);
		g.fill(x, y + TITLE_H, x + W, y + TITLE_H + 1, Theme.BORDER);
		text(g, "dqrkz", x + 6, y + 5, Theme.ACCENT);
		text(g, "client", x + 6 + font.width("dqrkz "), y + 5, Theme.TEXT_DIM);

		int bodyTop = y + TITLE_H + 1;
		int bodyBottom = y + H - FOOTER_H;

		// Sidebar
		g.fill(x, bodyTop, x + SIDEBAR_W, bodyBottom, Theme.SIDEBAR_BG);
		g.fill(x + SIDEBAR_W, bodyTop, x + SIDEBAR_W + 1, bodyBottom, Theme.BORDER);

		for (int i = 0; i < Menu.TABS.size(); i++) {
			int ty = bodyTop + 6 + i * TAB_H;
			boolean selected = i == selectedTab;
			boolean hovered = mouseX >= x && mouseX < x + SIDEBAR_W && mouseY >= ty && mouseY < ty + TAB_H;

			if (selected) {
				g.fill(x, ty, x + SIDEBAR_W, ty + TAB_H, Theme.BG);
				g.fill(x, ty, x + 2, ty + TAB_H, Theme.ACCENT);
			}
			int color = selected ? Theme.ACCENT : hovered ? Theme.TEXT_HOVER : Theme.TEXT_DIM;
			text(g, Menu.TABS.get(i).name, x + 12, ty + (TAB_H - 8) / 2, color);
		}

		// Footer
		g.fill(x, bodyBottom, x + W, bodyBottom + 1, Theme.BORDER);
		g.fill(x, bodyBottom + 1, x + W, y + H, Theme.TITLE_BG);
		text(g, "RSHIFT to close", x + 6, bodyBottom + 4, Theme.TEXT_DIM);
		String ver = "1.21.11";
		text(g, ver, x + W - 6 - font.width(ver), bodyBottom + 4, Theme.TEXT_DIM);

		// Content
		Tab tab = Menu.TABS.get(selectedTab);
		int viewLeft = x + SIDEBAR_W + 1;
		int viewH = bodyBottom - bodyTop;
		int contentH = contentHeight(tab);
		int maxScroll = Math.max(0, contentH - viewH);
		scroll = Math.max(0, Math.min(scroll, maxScroll));

		int contentX = viewLeft + PAD;
		int colW = (W - SIDEBAR_W - 1 - PAD * 3) / 2;
		int startY = bodyTop + PAD + 4 - scroll;
		int[] colY = {startY, startY};

		g.enableScissor(viewLeft, bodyTop, x + W, bodyBottom);

		if (tab.groups.isEmpty()) {
			text(g, "Nothing here yet", contentX, bodyTop + PAD + 4, Theme.TEXT_DIM);
		}

		for (Group group : tab.groups) {
			int gx = contentX + group.column * (colW + PAD);
			int gy = colY[group.column];
			int gh = groupHeight(group);

			outline(g, gx, gy, colW, gh, Theme.BORDER);
			int tw = font.width(group.title);
			g.fill(gx + 5, gy, gx + 9 + tw, gy + 1, Theme.BG);
			text(g, group.title, gx + 7, gy - 3, Theme.TEXT_DIM);

			int sy = gy + GROUP_HEAD;
			for (Setting s : group.settings) {
				s.x = gx + 8;
				s.y = sy;
				s.w = colW - 16;
				renderSetting(g, s, mouseX, mouseY);
				sy += s.height();
			}

			colY[group.column] += gh + GROUP_GAP;
		}

		g.disableScissor();

		// Scrollbar
		if (maxScroll > 0) {
			int thumbH = Math.max(12, viewH * viewH / contentH);
			int thumbY = (int) ((viewH - thumbH) * (scroll / (double) maxScroll));
			g.fill(x + W - 3, bodyTop + thumbY, x + W - 1, bodyTop + thumbY + thumbH, Theme.SCROLL);
		}
	}

	private void renderSetting(GuiGraphics g, Setting s, int mouseX, int mouseY) {
		boolean hovered = s.hit(mouseX, mouseY);

		if (s instanceof BoolSetting b) {
			int bx = s.x, by = s.y + 2;
			g.fill(bx, by, bx + 8, by + 8, Theme.BORDER);
			g.fill(bx + 1, by + 1, bx + 7, by + 7, b.value ? Theme.ACCENT : Theme.ELEMENT_BG);
			text(g, s.name, s.x + 13, s.y + 2, hovered ? Theme.TEXT_HOVER : Theme.TEXT);

		} else if (s instanceof SliderSetting sl) {
			text(g, sl.name, s.x, s.y + 1, hovered ? Theme.TEXT_HOVER : Theme.TEXT);
			String val = sl.display();
			text(g, val, s.x + s.w - font.width(val), s.y + 1, Theme.TEXT_DIM);

			int by = s.y + 12;
			g.fill(s.x, by, s.x + s.w, by + 5, Theme.BORDER);
			g.fill(s.x + 1, by + 1, s.x + s.w - 1, by + 4, Theme.ELEMENT_BG);
			int filled = Math.round((s.w - 2) * sl.fraction());
			if (filled > 0) g.fill(s.x + 1, by + 1, s.x + 1 + filled, by + 4, Theme.ACCENT);
		}
	}

	// ------------------------------------------------------------------ input

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if (event.button() != 0) return super.mouseClicked(event, doubleClick);

		double mx = event.x(), my = event.y();
		if (mx < winX || mx >= winX + W || my < winY || my >= winY + H) return true;

		// Title bar -> drag
		if (my < winY + TITLE_H) {
			draggingWindow = true;
			dragOffX = (int) (mx - winX);
			dragOffY = (int) (my - winY);
			return true;
		}

		int bodyTop = winY + TITLE_H + 1;
		int bodyBottom = winY + H - FOOTER_H;

		// Sidebar tabs
		if (mx < winX + SIDEBAR_W) {
			for (int i = 0; i < Menu.TABS.size(); i++) {
				int ty = bodyTop + 6 + i * TAB_H;
				if (my >= ty && my < ty + TAB_H) {
					if (selectedTab != i) scroll = 0;
					selectedTab = i;
					return true;
				}
			}
			return true;
		}

		// Settings (only inside the visible content area)
		if (my < bodyTop || my >= bodyBottom) return true;

		for (Group group : Menu.TABS.get(selectedTab).groups) {
			for (Setting s : group.settings) {
				if (!s.hit(mx, my)) continue;
				if (s instanceof BoolSetting b) {
					b.value = !b.value;
				} else if (s instanceof SliderSetting sl) {
					activeSlider = sl;
					sl.setFromMouse(mx);
				}
				return true;
			}
		}
		return true;
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
		if (draggingWindow) {
			winX = (int) (event.x() - dragOffX);
			winY = (int) (event.y() - dragOffY);
			clampWindow();
			return true;
		}
		if (activeSlider != null) {
			activeSlider.setFromMouse(event.x());
			return true;
		}
		return super.mouseDragged(event, dragX, dragY);
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		draggingWindow = false;
		activeSlider = null;
		return super.mouseReleased(event);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		if (mouseX >= winX && mouseX < winX + W && mouseY >= winY && mouseY < winY + H) {
			scroll = Math.max(0, Math.min(scroll - (int) (scrollY * 12), maxScroll()));
			return true;
		}
		return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (event.key() == GLFW.GLFW_KEY_RIGHT_SHIFT) {
			onClose();
			return true;
		}
		return super.keyPressed(event);
	}
}