package io.ejekta.makkit.client.editor.input

import io.ejekta.makkit.client.editor.IEditor
import me.shedaniel.clothconfig2.api.Modifier
import me.shedaniel.clothconfig2.api.ModifierKeyCode
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.InputUtil

class KeyStateHandler(val id: String, var binding: ModifierKeyCode) : IEditor {

    var isDown = false
        private set

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
        if (!isDown && (binding.matchesCurrentKey() || binding.matchesCurrentMouse())) {
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