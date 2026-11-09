package me.glokmc.client;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** ESC menüsündeki "GlokMC Ayarları" düğmesinin açtığı panel. */
public class GlokSettingsScreen extends Screen {
	private static final String[] CATS = {"Tümü", "Görsel", "Arayüz", "Oyuncu"};

	private record Feature(String name, int cat, BooleanSupplier get, Consumer<Boolean> set) {
	}

	private static final List<Feature> FEATURES = List.of(
		new Feature("FPS Göstergesi", 2, () -> Config.fpsHud, v -> Config.fpsHud = v),
		new Feature("Tuş Göstergesi", 2, () -> Config.keystrokes, v -> Config.keystrokes = v),
		new Feature("Tam Parlaklık", 1, () -> Config.fullbright, v -> Config.fullbright = v),
		new Feature("Zoom (C tuşu)", 3, () -> Config.zoom, v -> Config.zoom = v),
		new Feature("Koşu Kilidi", 3, () -> Config.sprintLock, v -> {
			Config.sprintLock = v;
			Minecraft.getInstance().options.toggleSprint().set(v);
		})
	);

	private final Screen parent;
	private int cat = 0;

	public GlokSettingsScreen(Screen parent) {
		super(Component.literal("GlokMC Ayarları"));
		this.parent = parent;
	}

	private static Component label(Feature f) {
		return Component.literal(f.name() + ": " + (f.get().getAsBoolean() ? "AÇIK" : "KAPALI"));
	}

	@Override
	protected void init() {
		int px = this.width / 2 - 210;
		int py = this.height / 2 - 120;

		for (int i = 0; i < CATS.length; i++) {
			final int idx = i;
			addRenderableWidget(Button.builder(
				Component.literal((i == cat ? "> " : "") + CATS[i]),
				b -> {
					cat = idx;
					rebuildWidgets();
				}).bounds(px + 10, py + 44 + i * 24, 96, 20).build());
		}

		int row = 0;
		for (Feature f : FEATURES) {
			if (cat != 0 && f.cat() != cat) continue;
			addRenderableWidget(Button.builder(label(f), b -> {
				f.set().accept(!f.get().getAsBoolean());
				Config.save();
				b.setMessage(label(f));
			}).bounds(px + 116, py + 44 + row * 24, 294, 20).build());
			row++;
		}

		addRenderableWidget(Button.builder(Component.literal("Kapat"), b -> onClose())
			.bounds(px + 116, py + 206, 294, 20).build());
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
		int px = this.width / 2 - 210;
		int py = this.height / 2 - 120;
		g.fill(px, py, px + 420, py + 240, 0xF0101424);
		g.fill(px, py, px + 420, py + 3, 0xFF35D9FF);
		g.fill(px + 108, py + 40, px + 109, py + 232, 0xFF2A3050);
		g.text(this.font, "GLOKMC", px + 12, py + 12, 0xFF35D9FF, true);
		g.text(this.font, "Client Ayarları", px + 12, py + 24, 0xFF8D97B5, false);
		g.text(this.font, "Menü: Sağ Shift  •  Zoom: C", px + 116, py + 232 - 6, 0xFF8D97B5, false);
		super.extractRenderState(g, mouseX, mouseY, delta);
	}

	@Override
	public void onClose() {
		this.minecraft.setScreen(parent);
	}
}
