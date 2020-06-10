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

        if (!MakkitClient.config.showLegend) {
            return
        }

        setupDraw(inStack)

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
            drawKeybinds(*keysToUse.toTypedArray())
        }

        drawAllText()
    }

    private fun addText(newText: Text) {
        text.add(Triple(newText, null, null))
    }

    private fun drawKeybinds(vararg handlers: KeyStateHandler) {
        val longestName = handlers.maxBy {
            renderer.getWidth(it.name)
        }!!
        handlers.forEachIndexed { _, handler ->
            text.add(Triple(
                    handler.shortName,
                    renderer.getWidth(longestName.name) + 5,
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
        val rx = width - (renderer.getWidth(text) + padding + offset)
        val ty = (fontSize * (this.text.size - line))
        val by = height - (fontSize * (line + 2.5f))

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