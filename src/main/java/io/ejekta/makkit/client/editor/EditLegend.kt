package io.ejekta.makkit.client.editor

import io.ejekta.makkit.client.MakkitClient
import io.ejekta.makkit.client.editor.input.KeyStateHandler
import io.ejekta.makkit.common.enums.GuiCorner
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.text.TextColor
import net.minecraft.util.Formatting
import net.minecraft.util.math.MathHelper

// The textual legend on the bottom left of the screen
object EditLegend {

    private data class LegendLine(val text: Text? = null, val key: KeyStateHandler? = null, var lastHit: Long = 0L)

    private val mc = MinecraftClient.getInstance()
    private val renderer = MinecraftClient.getInstance().textRenderer
    private val fontSize = renderer.fontHeight.toFloat()
    private val width: Float
        get() = mc.window.scaledWidth.toFloat()
    private val height: Float
        get() = mc.window.scaledHeight.toFloat()
    private var stack: MatrixStack = MatrixStack()
    private val texts = mutableListOf<LegendLine>()
    private var longestNameLength = 0

    private fun addText(newText: Text) {
        texts.add(LegendLine(newText))
    }

    init {
        populateLegend()
    }

    fun populateLegend() {
        texts.clear()

        addText(LiteralText("Makkit Key Legend: "))
        addText(LiteralText("==============="))

        val keysToUse = mutableListOf<KeyStateHandler>()

        MakkitClient.config.run {
            if (showUtility) {
                keysToUse.addAll(listOf(
                        newBoxKey,
                        moveDragKey,
                        movePushKey,
                        resizeSideKey,
                        resizeSymmetricKey
                ))
            }

            if (showBasic) {
                keysToUse.addAll(listOf(
                        placeMode,
                        fillKey,
                        wallsKey,
                        repeatPatternKey,
                        multiPalette,
                        mirrorToolKey,
                        airMode
                ))
            }

            if (showSystem) {
                keysToUse.addAll(listOf(
                        copyKey,
                        pasteKey,
                        undoKey,
                        redoKey
                ))
            }

        }

        if (keysToUse.isNotEmpty()) {
            val longestName = keysToUse.maxBy { renderer.getWidth(it.name) }!!
            longestNameLength = renderer.getWidth(longestName.name)

            for (handler in keysToUse) {
                texts.add(LegendLine(null, handler))
            }
        }

    }

    fun draw(inStack: MatrixStack) {

        stack = inStack

        if (!MakkitClient.config.showLegend) {
            return
        }

        drawAllText()
    }




    private fun drawAllText() {
        texts.reversed().forEachIndexed { i, line ->
            when {
                line.text != null -> drawTextOn(i, line, line.text, 0)
                line.key != null -> {
                    val isDown = line.key.isDown
                    if (isDown) {
                        line.lastHit = System.currentTimeMillis()
                    }
                    drawTextOn(i, line, line.key.shortName, 0, isDown)
                    drawTextOn(i, line, line.key.binding.localizedName, longestNameLength + 5, false)
                }
            }
        }
    }

    private fun drawTextOn(lineNum: Int, line: LegendLine, text: Text, offset: Int, isDown: Boolean = false) {

        val fadeTime = 900f // millis

        val color = MathHelper.hsvToRgb(
                0.13f,
                (fadeTime - (System.currentTimeMillis() - line.lastHit).toFloat().coerceIn(0f..fadeTime)) / fadeTime,
                1f
        )

        val textToUse = text.copy().styled {
            it.withColor(TextColor.fromRgb(color))
        }

        val enum = MakkitClient.config.legendCorner
        val padding = 2f

        val lx = padding + offset
        val rx = width - (renderer.getWidth(textToUse) + padding + offset)
        val ty = (fontSize * (this.texts.size - lineNum))
        val by = height - (fontSize * (lineNum + 2.5f))

        val corner = when (enum) {
            GuiCorner.TOP_LEFT -> lx to ty
            GuiCorner.TOP_RIGHT -> rx to ty
            GuiCorner.BOTTOM_RIGHT -> rx to by
            GuiCorner.BOTTOM_LEFT -> lx to by
        }

        drawText(
                textToUse,
                corner.first,
                corner.second
        )
    }

    private fun drawText(text: Text, x: Float, y: Float) {
        val color = when (MakkitClient.isInEditMode) {
            true -> 0xFFFFFF
            false -> 0xAAAAAA
        }
        renderer.draw(stack, text, x, y, color)
    }

}