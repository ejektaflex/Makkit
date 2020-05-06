package ejektaflex.makkit.client

import ejektaflex.makkit.client.editor.EditRegion
import ejektaflex.makkit.client.editor.input.InputState
import ejektaflex.makkit.client.editor.input.KeyStateHandler
import ejektaflex.makkit.client.editor.input.MakkitKeys
import ejektaflex.makkit.client.event.Events
import ejektaflex.makkit.client.keys.KeyRemapper
import ejektaflex.makkit.common.world.WorldOperation
import ejektaflex.makkit.client.render.RenderHelper
import ejektaflex.makkit.common.enum.UndoRedoMode
import ejektaflex.makkit.common.network.pakkits.EditHistoryPacket
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry
import net.minecraft.client.util.InputUtil
import net.minecraft.util.Identifier
import org.lwjgl.glfw.GLFW

class MakkitClient : ClientModInitializer {

    val region = EditRegion().apply {
        moveTo(4, 4, 4, 4, 3, 2)
    }

    override fun onInitializeClient() {

        MakkitKeys.setup()

        MakkitKeys.apply {
            fillBinding.setKeyDown {
                region.doOperation(WorldOperation.FILL)
            }

            wallsBinding.setKeyDown {
                region.doOperation(WorldOperation.WALLS)
            }

            undoButton.setKeyDown {
                EditHistoryPacket(UndoRedoMode.UNDO).sendToServer()
            }

            redoButton.setKeyDown {
                EditHistoryPacket(UndoRedoMode.REDO).sendToServer()
            }
        }

        // Remap toolbar activators to '[' and ']'. These are rarely used and the player can view the controls
        // If they wish to see the new bindings.
        KeyRemapper.remap("key.saveToolbarActivator", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_BRACKET)
        KeyRemapper.remap("key.loadToolbarActivator", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_BRACKET)

        Events.DrawScreenEvent.Dispatcher.register(::onDrawScreen)
        Events.MouseScrollEvent.Dispatcher.register(::onScroll)
    }

    private fun onScroll(e: Events.MouseScrollEvent) {
        println("Scrolled: ${e.amount}!")
    }

    private fun onDrawScreen(e: Events.DrawScreenEvent) {
        // RenderHelper state
        RenderHelper.setState(e.matrices, e.tickDelta, e.camera, e.buffers, e.matrix)

        for (handler in MakkitKeys.keyHandlers) {
            handler.update()
        }

        InputState.update()

        RenderHelper.drawInWorld {
            region.update()
            region.draw()
        }
    }

}