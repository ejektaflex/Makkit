package io.ejekta.makkit.client.mixin;

import io.ejekta.makkit.client.MakkitClient;
import io.ejekta.makkit.client.editor.input.ClientPalette;
import io.ejekta.makkit.client.render.RenderTextHelper;
import io.ejekta.makkit.common.MakkitCommon;
import io.ejekta.makkit.common.editor.data.BlockPalette;
import io.ejekta.makkit.common.enums.AirFillOption;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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

    Identifier SELECTION = new Identifier(MakkitCommon.ID, "textures/misc/palette_select.png");

    @Environment(EnvType.CLIENT)
    @Inject(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;setZOffset(I)V", ordinal = 1), cancellable = true)
    private void renderHotbar(float f, MatrixStack matrixStack, CallbackInfo ci) {
        PlayerEntity playerEntity = getCameraPlayer();
        client.getTextureManager().bindTexture(SELECTION);

        int i = scaledWidth / 2;

        for (int hotNum = 0; hotNum < 9; hotNum++) {
            ItemStack stack = playerEntity.inventory.getStack(hotNum);
            if (ClientPalette.INSTANCE.hasStack(hotNum)) {

                MinecraftClient.getInstance().inGameHud.drawTexture(
                        matrixStack,
                        i - 92 + hotNum * 20,
                        this.scaledHeight - 23,
                        0,
                        0,
                        24,
                        22
                );

                // If test fails, add a red background
                if (BlockPalette.Companion.test(stack) == null) {
                    MinecraftClient.getInstance().inGameHud.drawTexture(
                            matrixStack,
                            i - 92 + hotNum * 20,
                            this.scaledHeight - 23,
                            0,
                            44,
                            24,
                            22
                    );
                }



            }
        }

        if (ClientPalette.INSTANCE.hasAnyItems()) {
            MinecraftClient.getInstance().inGameHud
                    .drawTexture(
                            matrixStack,
                            i - 91 - 1 + playerEntity.inventory.selectedSlot * 20,
                            this.scaledHeight - 23,
                            0,
                            22,
                            24,
                            22
                    );
        }

        AirFillOption opt = MakkitClient.Companion.getAirModeOption();
        int airPos = 4;

        if (opt == AirFillOption.ONLY_AIR) {
            MinecraftClient.getInstance().inGameHud
                    .drawTexture(
                            matrixStack,
                            i - 12,
                            6,
                            24,
                            0,
                            24,
                            22
                    );

            LiteralText text = new LiteralText("Only Affect Air");
            RenderTextHelper.INSTANCE.drawTextCentered(matrixStack, text, i, 2, 0xFFFFFF);

        } else if(opt == AirFillOption.EXCLUDE_AIR) {
            MinecraftClient.getInstance().inGameHud
                    .drawTexture(
                            matrixStack,
                            i - 12,
                            6,
                            48,
                            0,
                            24,
                            22
                    );

            LiteralText text = new LiteralText("Don't Affect Air");
            RenderTextHelper.INSTANCE.drawTextCentered(matrixStack, text, i, 2, 0xFFFFFF);
        }



        // Bind back to widgets texture
        client.getTextureManager().bindTexture(WIDGETS_TEX);


        //System.out.println("Yeah!!");
    }


}
