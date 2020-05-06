package ejektaflex.makkit.client.editor.input

import ejektaflex.makkit.client.MakkitClient
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.InputUtil
import net.minecraft.util.Identifier
import org.lwjgl.glfw.GLFW

object InputState {



    private val opts = MinecraftClient.getInstance().options

    init {
        MakkitKeys.toggleBackBinding.setKeyDown {
            isBackSelecting = !isBackSelecting
        }
    }

    var isBackSelecting: Boolean = false
        private set

    fun update() {

    }




}