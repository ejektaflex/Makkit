package ejektaflex.makkit.client.editor.input

import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry
import net.minecraft.client.util.InputUtil
import net.minecraft.util.Identifier
import org.lwjgl.glfw.GLFW

object MakkitKeys {

    fun setup() {
        KeyBindingRegistry.INSTANCE.apply {
            addCategory("Makkit")
            for (bindHandler in keyHandlers) {
                register(bindHandler.binding)
            }
        }
    }

    val moveDragBinding = KeyStateHandler(
            FabricKeyBinding.Builder.create(
                    Identifier("makkit", "move_dual_axis"),
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_Z,
                    "Makkit"
            ).build()
    )

    val resizeSideBinding = KeyStateHandler(
            FabricKeyBinding.Builder.create(
                    Identifier("makkit", "resize_single_axis"),
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_C,
                    "Makkit"
            ).build()
    )

    val fillBinding = KeyStateHandler(
            FabricKeyBinding.Builder.create(
                    Identifier("makkit", "fill"),
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_R,
                    "Makkit"
            ).build()
    )

    val wallsBinding = KeyStateHandler(
            FabricKeyBinding.Builder.create(
                    Identifier("makkit", "fill_walls"),
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_V,
                    "Makkit"
            ).build()
    )

    val toggleBackBinding = KeyStateHandler(
            FabricKeyBinding.Builder.create(
                    Identifier("makkit", "toggle_back_selection"),
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_LEFT_ALT,
                    "Makkit"
            ).build()
    )

    val holdBackBinding = KeyStateHandler(
            FabricKeyBinding.Builder.create(
                    Identifier("makkit", "hold_back_selection"),
                    InputUtil.Type.MOUSE,
                    GLFW.GLFW_MOUSE_BUTTON_5,
                    "Makkit"
            ).build()
    )

    val undoButton = KeyStateHandler(
            FabricKeyBinding.Builder.create(
                    Identifier("makkit", "undo"),
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_COMMA,
                    "Makkit"
            ).build()
    )

    val redoButton = KeyStateHandler(
            FabricKeyBinding.Builder.create(
                    Identifier("makkit", "redo"),
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_PERIOD,
                    "Makkit"
            ).build()
    )

    val keyHandlers = listOf(
            moveDragBinding,
            resizeSideBinding,
            fillBinding,
            wallsBinding,
            toggleBackBinding,
            holdBackBinding,
            undoButton,
            redoButton
    )

}