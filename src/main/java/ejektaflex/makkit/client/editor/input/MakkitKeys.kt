package ejektaflex.makkit.client.editor.input

import ejektaflex.makkit.client.MakkitClient
import ejektaflex.makkit.common.enum.UndoRedoMode
import ejektaflex.makkit.common.network.pakkits.server.EditHistoryPacket
import ejektaflex.makkit.common.world.WorldOperation
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.InputUtil
import net.minecraft.util.Identifier
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import org.lwjgl.glfw.GLFW

object MakkitKeys {

    fun setup() {
        KeyBindingRegistry.INSTANCE.apply {
            addCategory("Makkit")
            for (bindHandler in keyHandlers) {
                register(bindHandler.binding)
            }
        }

        fillBinding.setKeyDown {
            MakkitClient.region?.doOperation(WorldOperation.SET)
        }

        wallsBinding.setKeyDown {
            MakkitClient.region?.doOperation(WorldOperation.WALLS)
        }

        undoButton.setKeyDown {
            EditHistoryPacket(UndoRedoMode.UNDO).sendToServer()
        }

        redoButton.setKeyDown {
            EditHistoryPacket(UndoRedoMode.REDO).sendToServer()
        }

        centerRegionBinding.setKeyDown {
            val btr = MinecraftClient.getInstance().crosshairTarget
            if (btr != null && btr.type == HitResult.Type.BLOCK) {
                MakkitClient.getOrCreateRegion().centerOn(BlockPos(btr.pos))
            }
        }

    }

    private fun makkitKey(path: String, type: InputUtil.Type, code: Int): KeyStateHandler {
        return KeyStateHandler(
                FabricKeyBinding.Builder.create(
                        Identifier(MakkitClient.ID, path),
                        type,
                        code,
                        "Makkit"
                ).build()
        )
    }

    val moveDragBinding = makkitKey("move_dual_axis", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_Z)
    val resizeSideBinding = makkitKey("resize_single_axis", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_C)
    val fillBinding = makkitKey("fill", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R)
    val wallsBinding = makkitKey("fill_walls", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V)

    val toggleBackBinding = makkitKey("toggle_back_selection", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT)
    val holdBackBinding = makkitKey("hold_back_selection", InputUtil.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_5)

    val centerRegionBinding = makkitKey("center_edit_region", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_B)

    val undoButton = makkitKey("undo", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_COMMA)
    val redoButton = makkitKey("redo", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_PERIOD)

    val keyHandlers = listOf(
            moveDragBinding,
            resizeSideBinding,
            fillBinding,
            wallsBinding,
            toggleBackBinding,
            holdBackBinding,
            undoButton,
            redoButton,
            centerRegionBinding
    )

}