package ejektaflex.makkit.client.editor.input

import ejektaflex.makkit.client.MakkitClient
import net.minecraft.client.MinecraftClient

object InputState {

    private val opts = MinecraftClient.getInstance().options

    init {
        MakkitClient.toggleBackBinding.setKeyDown {
            isBackSelecting = !isBackSelecting
        }
    }

    var isBackSelecting: Boolean = false
        private set

    fun update() {

    }

}