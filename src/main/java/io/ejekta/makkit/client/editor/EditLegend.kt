package io.ejekta.makkit.client.editor

import io.ejekta.makkit.client.MakkitClient
import io.ejekta.makkit.client.editor.input.KeyStateHandler
import io.ejekta.makkit.common.enums.GuiCorner
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.LiteralText
import net.minecraft.text.Text

// The textual legend on the bottom left of the screen
object EditLegend {

    private val mc = MinecraftClient.getInstance()
    private val renderer = MinecraftClient.getInstance().textRenderer
    private val fontSize = renderer.fontHeight.toFloat()
    private val width: Float
        get() = mc.window.scaledWidth.toFloat()
    private val height: Float
        get() = mc.window.scaledHeight.toFloat()
    private var stack: MatrixStack = MatrixStack()
    private val text = mutableListOf<Triple<Text, Int?, Text?>>()

    fun setupDraw(inStack: MatrixStack) {
        stack = inStack
        text.clear()
    }


    fun draw(inStack: MatrixStack) {
        setupDraw(inStack)

        addText(LiteralText("Makkit Key Legend: "))
        addText(LiteralText("============"))
        addText(LiteralText("Air Mode: ${MakkitClient.airModeOption}"))
        addText(LiteralText("============"))

        MakkitClient.config.run {
            if (showUtility) {
                drawKeybinds(
                        newBoxKey,
                        moveDragKey,
                        resizeSideKey,
                        resizeSymmetricKey
                )
            }

            if (showBasic) {
                drawKeybinds(
                        editMode,
                        fillKey,
                        wallsKey,
                        repeatPatternKey,
                        multiPalette,
                        mirrorToolKey,
                        airMode
                )
            }

            if (showSystem) {
                drawKeybinds(
                        copyKey,
                        pasteKey,
                        undoKey,
                        redoKey
                )
            }

        }


        drawAllText()
    }

    private fun addText(newText: Text, offset: Int = 0) {
        text.add(Triple(newText, null, null))
    }

    private fun drawKeybinds(vararg handlers: KeyStateHandler) {
        val longestName = handlers.maxBy {
            renderer.getStringWidth(it.name)
        }!!
        handlers.forEachIndexed { i, handler ->
            text.add(Triple(
                    handler.shortName,
                    renderer.getStringWidth(longestName.name) + 5,
                    handler.binding.localizedName
            ))
        }

    }

    private fun drawAllText() {
        text.reversed().forEachIndexed { i, text ->
            drawTextOn(i, text.first, 0)
            text.third?.let {
                drawTextOn(i, text.third!!, text.second!!)
            }
        }
    }

    private fun drawTextOn(line: Int, text: Text, offset: Int) {

        val enum = MakkitClient.config.legendCorner
        val padding = 2f

        val lx = padding + offset
        val rx = width - (renderer.getStringWidth(text) + padding + offset)
        val ty = (fontSize * (this.text.size - line))
        val by = height - (fontSize * (line + 1))

        val corner = when (enum) {
            GuiCorner.TOP_LEFT -> lx to ty
            GuiCorner.TOP_RIGHT -> rx to ty
            GuiCorner.BOTTOM_RIGHT -> rx to by
            GuiCorner.BOTTOM_LEFT -> lx to by
        }
        drawText(text, corner.first, corner.second)
    }

    private fun drawText(text: Text, x: Float, y: Float) {
        val color = when (MakkitClient.isInEditMode) {
            true -> 0xFFFFFF
            false -> 0xAAAAAA
        }
        renderer.draw(stack, text, x, y, color )
    }


}