package io.ejekta.makkit.client.render

import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.util.Window
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text

object RenderTextHelper {
    private val renderer: TextRenderer
        get() = MinecraftClient.getInstance().textRenderer

    private val window: Window
        get() = MinecraftClient.getInstance().window

    fun drawText(stack: MatrixStack, text: Text, x: Float, y: Float, color: Int) {
        renderer.draw(stack, text, x, y , color)
    }

    fun drawTextCentered(stack: MatrixStack, text: Text, x: Float, y: Float, color: Int = 0xFFFFFF) {
        renderer.draw(stack, text, x - renderer.getWidth(text) / 2, y , color)
    }
}