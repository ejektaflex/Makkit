package io.ejekta.makkit.client.editor.input

import io.ejekta.makkit.client.editor.IEditor
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding

class KeyStateHandler(val binding: FabricKeyBinding) : IEditor {

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
        if (!isDown && binding.isPressed) {
            isDown = true
            onKeyDown()
        }

        // Try to stop dragging
        if (isDown && !binding.isPressed) {
            onKeyUp()
            isDown = false
        }

    }

}