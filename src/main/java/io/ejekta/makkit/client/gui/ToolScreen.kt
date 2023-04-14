package io.ejekta.makkit.client.gui

import io.ejekta.kambrik.KambrikScreen
import net.minecraft.client.MinecraftClient
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

    override fun onDrawBackground(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        //gui.draw(matrices, mouseX, mouseY, delta)
    }

    override fun onDrawForeground(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        gui.draw(matrices, mouseX, mouseY, delta)
    }


}