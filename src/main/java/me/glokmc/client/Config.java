package me.glokmc.client;

import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.fabricmc.loader.api.FabricLoader;

/** Ayarlar config/glokmc-client.json dosyasında saklanır. */
public final class Config {
	public static boolean fpsHud = true;
	public static boolean keystrokes = false;
	public static boolean fullbright = false;
	public static boolean zoom = true;
	public static boolean sprintLock = false;
	/** Zoom yapılırken kullanılacak görüş açısı (Minecraft en az 30'a izin verir). */
	public static int zoomFov = 30;

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private static class Data {
		boolean fpsHud = true, keystrokes = false, fullbright = false, zoom = true, sprintLock = false;
	}

	private static Path file() {
		return FabricLoader.getInstance().getConfigDir().resolve("glokmc-client.json");
	}

	public static void load() {
		try {
			Path p = file();
			if (!Files.exists(p)) return;
			Data d = GSON.fromJson(Files.readString(p), Data.class);
			if (d == null) return;
			fpsHud = d.fpsHud; keystrokes = d.keystrokes; fullbright = d.fullbright; zoom = d.zoom; sprintLock = d.sprintLock;
		} catch (Exception ignored) {
		}
	}

	public static void save() {
		try {
			Data d = new Data();
			d.fpsHud = fpsHud; d.keystrokes = keystrokes; d.fullbright = fullbright; d.zoom = zoom; d.sprintLock = sprintLock;
			Files.writeString(file(), GSON.toJson(d));
		} catch (Exception ignored) {
		}
	}

	private Config() {
	}
}
