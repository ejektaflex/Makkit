package io.ejekta.makkit.client.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.ejekta.makkit.client.editor.input.ItemPalette;
import io.ejekta.makkit.client.event.Events;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class ItemHotbarRenderMixin {


    @Shadow private int scaledWidth;

    @Shadow private int scaledHeight;

    @Shadow protected abstract PlayerEntity getCameraPlayer();

    @Shadow @Final private MinecraftClient client;

    @Shadow @Final private static Identifier WIDGETS_TEX;

    @Shadow protected abstract void drawTextBackground(MatrixStack matrixStack, TextRenderer textRenderer, int i, int j, int k);

    @Shadow public abstract TextRenderer getFontRenderer();

    Identifier SELECTION = new Identifier("makkit", "textures/misc/palette_select.png");

    @Environment(EnvType.CLIENT)
    @Inject(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;setZOffset(I)V", ordinal = 1), cancellable = true)
    private void renderHotbar(float f, MatrixStack matrixStack, CallbackInfo ci) {
        PlayerEntity playerEntity = getCameraPlayer();
        client.getTextureManager().bindTexture(SELECTION);

        int i = scaledWidth / 2;

        for (int hotNum = 0; hotNum < 9; hotNum++) {
            ItemStack stack = playerEntity.inventory.getStack(hotNum);
            if (ItemPalette.INSTANCE.hasStack(stack)) {

                MinecraftClient.getInstance().inGameHud
                        .drawTexture(
                                matrixStack,
                                i - 91 - 1 + hotNum * 20,
                                this.scaledHeight - 22 - 1,
                                0,
                                0,
                                24,
                                22
                        );

            }
        }

        if (ItemPalette.INSTANCE.hasAnyItems()) {
            MinecraftClient.getInstance().inGameHud
                    .drawTexture(
                            matrixStack,
                            i - 91 - 1 + playerEntity.inventory.selectedSlot * 20,
                            this.scaledHeight - 22 - 1,
                            0,
                            22,
                            24,
                            22
                    );
        }

        // Bind back to widgets texture
        client.getTextureManager().bindTexture(WIDGETS_TEX);


        //System.out.println("Yeah!!");
    }


}
