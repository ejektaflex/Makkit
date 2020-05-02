package ejektaflex.kalpis

import ejektaflex.kalpis.client.editor.EditRegion
import ejektaflex.kalpis.client.editor.input.InputState
import ejektaflex.kalpis.client.editor.input.KeyStateHandler
import ejektaflex.kalpis.client.event.Events
import ejektaflex.kalpis.common.io.StructureHelper
import ejektaflex.kalpis.client.keys.KeyRemapper
import ejektaflex.kalpis.render.RenderHelper
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.InputUtil
import net.minecraft.util.Identifier
import org.lwjgl.glfw.GLFW

class ExampleMod : ModInitializer {

    val mc: MinecraftClient = MinecraftClient.getInstance()


    val region = EditRegion().apply {
        moveTo(4, 4, 4, 4, 3, 2)
    }

    override fun onInitialize() {

        println("Hello Fabric world!")

        KeyBindingRegistry.INSTANCE.apply {
            addCategory("KEdit")
            for (bindHandler in keyHandlers) {
                register(bindHandler.binding)
            }
        }

        saveHandler.setKeyDown {
            StructureHelper.saveStructure(region.area, Identifier("kedit", "doot"))
        }

        // Remap toolbar activators to '[' and ']'. These are rarely used and the player can view the controls
        // If they wish to see the new bindings.
        KeyRemapper.remap("key.saveToolbarActivator", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_BRACKET)
        KeyRemapper.remap("key.loadToolbarActivator", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_BRACKET)

        Events.DrawScreenEvent.Dispatcher.register(::onDrawScreen)

    }


    private fun onDrawScreen(e: Events.DrawScreenEvent) {
        // RenderHelper state
        RenderHelper.setState(e.matrices, e.tickDelta, e.camera, e.buffers, e.matrix)

        for (handler in keyHandlers) {
            handler.update()
        }

        InputState.update()

        RenderHelper.drawInWorld {
            region.update()
            region.draw()
        }
    }

    companion object {

        val moveDragBinding = KeyStateHandler(
                    FabricKeyBinding.Builder.create(
                    Identifier("kedit", "move_dual_axis"),
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_Z,
                    "KEdit"
            ).build()
        )

        val moveDragSingleBinding = KeyStateHandler(
                FabricKeyBinding.Builder.create(
                        Identifier("kedit", "resize_single_axis_opp"),
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_X,
                        "KEdit"
                ).build()
        )

        val resizeSideBinding = KeyStateHandler(
                FabricKeyBinding.Builder.create(
                        Identifier("kedit", "resize_single_axis"),
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_C,
                        "KEdit"
                ).build()
        )

        val resizeDualSideBinding = KeyStateHandler(
                FabricKeyBinding.Builder.create(
                        Identifier("kedit", "resize_dual_axis"),
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_V,
                        "KEdit"
                ).build()
        )

        val deleteBinding = KeyStateHandler(
                FabricKeyBinding.Builder.create(
                        Identifier("kedit", "fill/delete"),
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_BACKSPACE,
                        "KEdit"
                ).build()
        )

        val fillBinding = KeyStateHandler(
                FabricKeyBinding.Builder.create(
                        Identifier("kedit", "walls"),
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_R,
                        "KEdit"
                ).build()
        )

        val toggleBackBinding = KeyStateHandler(
                FabricKeyBinding.Builder.create(
                        Identifier("kedit", "toggle_back_selection"),
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_LEFT_ALT,
                        "KEdit"
                ).build()
        )


        val saveHandler = KeyStateHandler(
                FabricKeyBinding.Builder.create(
                        Identifier("kedit", "save_selection"),
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_M,
                        "KEdit"
                ).build()
        )

        val keyHandlers = listOf(
                moveDragBinding,
                moveDragSingleBinding, // TODO Fix inversion on negative face directions
                resizeSideBinding,
                //resizeDualSideBinding, // Awkward to use
                deleteBinding,
                fillBinding,
                toggleBackBinding,
                saveHandler
        )


    }


}