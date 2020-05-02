package ejektaflex.kalpis.edit.input

import ejektaflex.kalpis.ExampleMod
import net.minecraft.client.MinecraftClient

object InputState {

    private val opts = MinecraftClient.getInstance().options

    init {
        ExampleMod.toggleBackBinding.setKeyDown {
            isBackSelecting = !isBackSelecting
        }
    }

    var isBackSelecting: Boolean = false
        private set

    fun update() {

    }

}