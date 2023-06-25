package io.ejekta.makkit.client.gui

import io.ejekta.kambrik.gui.screen.KambrikScreen
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text

class ToolScreen : KambrikScreen(Text.literal("Tool Screen")) {

    init {
        resize(MinecraftClient.getInstance(), 400, 400)
    }

    val gui = kambrikGui {

        if (width != null && height != null) {
            rect(width, height, color = 0x008888) {

            }
        }

        rect(100, 100, color = 0xFF0000) {
            textImmediate(40, 40, Text.literal("Hi!"))
        }

    }

    override fun shouldPause(): Boolean {
        return false
    }

    override fun onDrawBackground(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {

    }

    override fun onDrawForeground(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        gui.draw(context, mouseX, mouseY, delta)
    }


}