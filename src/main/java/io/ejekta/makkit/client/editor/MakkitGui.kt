package io.ejekta.makkit.client.editor

import io.ejekta.makkit.client.MakkitClient
import io.ejekta.makkit.client.editor.input.ClientPalette.hasAnyItems
import io.ejekta.makkit.client.editor.input.ClientPalette.hasStack
import io.ejekta.makkit.client.render.RenderTextHelper.drawTextCentered
import io.ejekta.makkit.common.MakkitCommon
import io.ejekta.makkit.common.editor.data.BlockPalette
import io.ejekta.makkit.common.enums.BlockMask
import net.minecraft.block.Blocks
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.LiteralText
import net.minecraft.text.TextColor
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier

object MakkitGui {

    var SELECTION = Identifier(MakkitCommon.ID, "textures/misc/palette_select.png")

    private val mc = MinecraftClient.getInstance()

    private val playerEntity: PlayerEntity
        get() = mc.getCameraEntity() as PlayerEntity

    fun renderHotbarChanges(matrixStack: MatrixStack, width: Int, height: Int) {

        mc.textureManager.bindTexture(SELECTION)
        val i = width / 2

        fun highlightSlot(matrixStack: MatrixStack, num: Int) {
            val stack = playerEntity.inventory.getStack(num)
            if (hasStack(num)) {
                val x = i - 92 + num * 20
                drawAtlasIcon(matrixStack, x, height - 23, 0, 0)

                // If test fails, add a red background
                if (BlockPalette.testBlockOnly(stack) == null && (MakkitClient.region?.isBeingInteractedWith() != true
                                || !MakkitClient.isInEditMode)
                ) {
                    drawAtlasIcon(matrixStack, i - 92 + num * 20, height - 23, 24, 44)
                }

                if (BlockPalette.test(stack) == null) {
                    drawAtlasIcon(matrixStack, i - 92 + num * 20, height - 23, 0, 44)
                }

            }
        }

        // Hotbar render additions
        for (hotNum in 0..8) {
            highlightSlot(matrixStack, hotNum)
        }

        // Selected item overlay
        if (hasAnyItems()) {
            val selected = playerEntity.inventory.selectedSlot
            drawAtlasIcon(matrixStack, i - 92 + selected * 20, height - 23, 0, 22)
        }

    }



    fun drawAtlasIcon(matrixStack: MatrixStack, x: Int, y: Int, ax: Int, ay: Int) {
        MinecraftClient
                .getInstance()
                .inGameHud
                .drawTexture(
                        matrixStack,
                        x,
                        y,
                        ax,
                        ay,
                        24,
                        22
                )
    }


    fun renderBlockMaskGui(matrixStack: MatrixStack, width: Int, height: Int) {

        mc.textureManager.bindTexture(SELECTION)
        val i = width / 2

        // Block Mask data
        val opt = MakkitClient.blockMask

        when (opt) {
            BlockMask.ONLY_AIR -> {
                mc.inGameHud.drawTexture(matrixStack, i - 12, 6, 24, 0, 24, 22)
                drawTextCentered(matrixStack, opt.text, i.toFloat(), 2f)
            }
            BlockMask.NON_AIR -> {
                mc.inGameHud.drawTexture(matrixStack, i - 12, 6, 48, 0, 24, 22)
                drawTextCentered(matrixStack, opt.text, i.toFloat(), 2f)
            }
            BlockMask.OFFHAND -> {
                val stack = playerEntity.offHandStack
                val block = BlockPalette.test(stack)
                drawTextCentered(matrixStack, opt.text, i.toFloat(), 2f)

                if (block != null && block != Blocks.AIR) {
                    mc.itemRenderer.renderGuiItemIcon(playerEntity.offHandStack, i - 8, 12)
                } else {
                    val invalidText = LiteralText("(Invalid Offhand Item)").styled {
                        it.withColor(TextColor.fromFormatting(Formatting.RED))
                    }
                    drawTextCentered(matrixStack, invalidText, i.toFloat(), 12f)
                    //mc.itemRenderer.renderGuiItem(barrierItem, i - 8, 12)
                }

            }
            else -> {}
        }


    }

}