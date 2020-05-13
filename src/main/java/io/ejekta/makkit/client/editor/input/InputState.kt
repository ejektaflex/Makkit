package io.ejekta.makkit.client.editor.input

import net.minecraft.client.MinecraftClient

object InputState {

    private val opts = MinecraftClient.getInstance().options

    init {
        MakkitKeys.toggleBackBinding.setKeyDown {
            backSelectingToggle = !backSelectingToggle
        }
    }

    var backSelectingToggle: Boolean = false
        private set

    val isBackSelecting: Boolean
        get() = backSelectingToggle || MakkitKeys.holdBackBinding.isDown

    fun update() {

    }

}