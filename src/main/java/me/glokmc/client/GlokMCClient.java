package me.glokmc.client;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;

public class GlokMCClient implements ClientModInitializer {
	public static final KeyMapping.Category CATEGORY =
		KeyMapping.Category.register(Identifier.fromNamespaceAndPath("glokmc", "main"));

	private static KeyMapping zoomKey;
	private static KeyMapping menuKey;

	private static boolean zooming;
	private static int savedFov;
	private static boolean nightVisionOn;

	private static int frames;
	private static int fps;
	private static long lastSecond = System.nanoTime();

	@Override
	public void onInitializeClient() {
		Config.load();

		zoomKey = KeyMappingHelper.registerKeyMapping(new KeyMapping("key.glokmc.zoom", InputConstants.KEY_C, CATEGORY));
		menuKey = KeyMappingHelper.registerKeyMapping(new KeyMapping("key.glokmc.menu", InputConstants.KEY_RSHIFT, CATEGORY));

		ClientTickEvents.END_CLIENT_TICK.register(GlokMCClient::tick);

		HudElementRegistry.attachElementBefore(
			VanillaHudElements.CHAT,
			Identifier.fromNamespaceAndPath("glokmc", "hud"),
			GlokMCClient::drawHud
		);
	}

	private static void tick(Minecraft client) {
		while (menuKey.consumeClick()) {
			client.setScreen(new GlokSettingsScreen(null));
		}

		// Zoom: tuşa basılı tutunca görüş açısını daralt, bırakınca eski haline getir
		boolean wantZoom = Config.zoom && zoomKey.isDown() && client.screen == null;
		var fov = client.options.fov();
		if (wantZoom) {
			if (!zooming) {
				savedFov = fov.get();
				zooming = true;
			}
			fov.set(Math.min(savedFov, Math.max(30, Config.zoomFov)));
		} else if (zooming) {
			fov.set(savedFov);
			zooming = false;
		}

		// Tam parlaklık: oyuncuya sadece bu bilgisayarda görünen gece görüşü etkisi verir
		if (client.player != null) {
			if (Config.fullbright) {
				MobEffectInstance cur = client.player.getEffect(MobEffects.NIGHT_VISION);
				if (cur == null || cur.getDuration() < 240) {
					client.player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0, false, false, false));
				}
				nightVisionOn = true;
			} else if (nightVisionOn) {
				client.player.removeEffect(MobEffects.NIGHT_VISION);
				nightVisionOn = false;
			}
		}

		if (Config.sprintLock && !client.options.toggleSprint().get()) {
			client.options.toggleSprint().set(true);
		}
	}

	private static void drawHud(GuiGraphicsExtractor g, DeltaTracker tracker) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.options.hideGui) return;

		frames++;
		long now = System.nanoTime();
		if (now - lastSecond >= 1_000_000_000L) {
			fps = frames;
			frames = 0;
			lastSecond = now;
		}

		int y = 4;
		if (Config.fpsHud) {
			g.text(mc.font, fps + " FPS", 4, y, 0xFF35D9FF, true);
			y += 12;
		}
		if (Config.keystrokes) {
			drawKey(g, mc, 26, y, "W", mc.options.keyUp.isDown());
			drawKey(g, mc, 4, y + 22, "A", mc.options.keyLeft.isDown());
			drawKey(g, mc, 26, y + 22, "S", mc.options.keyDown.isDown());
			drawKey(g, mc, 48, y + 22, "D", mc.options.keyRight.isDown());
		}
	}

	private static void drawKey(GuiGraphicsExtractor g, Minecraft mc, int x, int y, String label, boolean down) {
		g.fill(x, y, x + 20, y + 20, down ? 0xCC35D9FF : 0x88000000);
		g.text(mc.font, label, x + 10 - mc.font.width(label) / 2, y + 6, down ? 0xFF000000 : 0xFFFFFFFF, false);
	}
}
