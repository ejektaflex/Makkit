package io.ejekta.makkit.client.editor.input

import io.ejekta.makkit.client.MakkitClient
import io.ejekta.makkit.common.MakkitCommon
import io.ejekta.makkit.common.enum.UndoRedoMode
import io.ejekta.makkit.common.network.pakkits.server.EditHistoryPacket
import io.ejekta.makkit.common.editor.operations.FillBlocksOperation
import io.ejekta.makkit.common.editor.operations.FillWallsOperation
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
            MakkitClient.region?.doOperation(FillBlocksOperation())
        }

        wallsBinding.setKeyDown {
            MakkitClient.region?.doOperation(FillWallsOperation())
        }

        undoButton.setKeyDown {
            EditHistoryPacket(UndoRedoMode.UNDO).sendToServer()
        }

        redoButton.setKeyDown {
            EditHistoryPacket(UndoRedoMode.REDO).sendToServer()
        }

        // Putting a selection at the cursor position
        centerRegionBinding.setKeyDown {
            val btr = MinecraftClient.getInstance().crosshairTarget
            if (btr != null && btr.type == HitResult.Type.BLOCK) {
                MakkitClient.getOrCreateRegion().centerOn(BlockPos(btr.pos))
            }
        }

    }

    val keyHandlers = mutableListOf<KeyStateHandler>()

    private fun makkitKey(path: String, type: InputUtil.Type, code: Int): KeyStateHandler {
        return KeyStateHandler(
                FabricKeyBinding.Builder.create(
                        Identifier(MakkitCommon.ID, path),
                        type,
                        code,
                        "Makkit"
                ).build()
        ).also {
            // Auto register keyhandler
            keyHandlers.add(it)
        }
    }

    val moveDragBinding = makkitKey("move_dual_axis", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_Z)
    val resizeSideBinding = makkitKey("resize_single_axis", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_C)
    val resizeSymmetricBinding = makkitKey("resize_single_axis_symmetric", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_X)
    val repeatPatternBinding = makkitKey("repeat_pattern", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G)
    val fillBinding = makkitKey("fill", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R)
    val wallsBinding = makkitKey("fill_walls", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V)
    val mirrorToolBinding = makkitKey("mirror_tool", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_N)

    val copyBinding = makkitKey("copy_tool", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_U)
    val pasteBinding = makkitKey("paste_tool", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_I)


    val toggleBackBinding = makkitKey("toggle_back_selection", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT)
    val holdBackBinding = makkitKey("hold_back_selection", InputUtil.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_5)

    val centerRegionBinding = makkitKey("center_edit_region", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_B)

    val undoButton = makkitKey("undo", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_COMMA)
    val redoButton = makkitKey("redo", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_PERIOD)


}