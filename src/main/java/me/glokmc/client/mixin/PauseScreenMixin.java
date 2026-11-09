package me.glokmc.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import me.glokmc.client.GlokSettingsScreen;

/** ESC menüsünün en üstüne "GlokMC Ayarları" düğmesini ekler, gerisine dokunmaz. */
@Mixin(PauseScreen.class)
public abstract class PauseScreenMixin extends Screen {
	protected PauseScreenMixin(Component title) {
		super(title);
	}

	@Inject(method = "init", at = @At("TAIL"))
	private void glokmc$addSettingsButton(CallbackInfo ci) {
		this.addRenderableWidget(Button.builder(
			Component.literal("GlokMC Ayarları"),
			b -> this.minecraft.setScreen(new GlokSettingsScreen((Screen) (Object) this))
		).bounds(this.width / 2 - 100, 12, 200, 20).build());
	}
}
