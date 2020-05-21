package io.ejekta.makkit.client.editor.input

import io.ejekta.makkit.client.editor.IEditor
import io.ejekta.makkit.common.MakkitCommon
import me.shedaniel.clothconfig2.api.ModifierKeyCode
import net.minecraft.client.MinecraftClient
import net.minecraft.text.MutableText
import net.minecraft.text.TranslatableText

class KeyStateHandler(val id: String, var binding: ModifierKeyCode) : IEditor {

    val isScreenOpen: Boolean
        get() = MinecraftClient.getInstance().currentScreen != null

    var isDown = false
        private set

    val name: MutableText
        get() = TranslatableText("${MakkitCommon.ID}.$id")

    val shortName: MutableText
        get() = TranslatableText("${MakkitCommon.ID}.$id.short")

    fun setKeyDown(func: () -> Unit) {
        onKeyDown = func
    }

    fun setKeyUp(func: () -> Unit) {
        onKeyUp = func
    }

    private var onKeyDown = {}

    private var onKeyUp = {}

    override fun update() {
        // Try to start dragging
        if (!isScreenOpen && !isDown && (binding.matchesCurrentKey() || binding.matchesCurrentMouse())) {
            isDown = true
            onKeyDown()
        }

        // Try to stop dragging
        if (isDown && (!binding.matchesCurrentKey() && !binding.matchesCurrentMouse())) {
            onKeyUp()
            isDown = false
        }

    }

}