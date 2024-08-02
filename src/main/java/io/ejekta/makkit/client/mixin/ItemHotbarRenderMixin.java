package io.ejekta.makkit.client.mixin;

import io.ejekta.makkit.client.editor.MakkitGui;
import io.ejekta.makkit.common.MakkitCommon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class ItemHotbarRenderMixin {


    @Shadow protected abstract PlayerEntity getCameraPlayer();

    @Shadow @Final private MinecraftClient client;

    @Shadow @Final private static Identifier HOTBAR_TEXTURE;

    //@Shadow protected abstract void drawTextBackground(MatrixStack matrixStack, TextRenderer textRenderer, int i, int j, int k);

    @Shadow public abstract TextRenderer getTextRenderer();

    Identifier SELECTION = Identifier.of(MakkitCommon.ID, "textures/misc/palette_select.png");

    @Environment(EnvType.CLIENT)
    @Inject(method = "renderHotbar", at = @At(value = "RETURN", ordinal = 1))
    private void renderHotbar(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {

        MakkitGui.INSTANCE.renderHotbarChanges(context, context.getScaledWindowWidth(), context.getScaledWindowHeight());

        MakkitGui.INSTANCE.renderBlockMaskGui(context, context.getScaledWindowWidth(), context.getScaledWindowHeight());

        // Bind back to widgets texture
        client.getTextureManager().bindTexture(HOTBAR_TEXTURE);

    }


}
