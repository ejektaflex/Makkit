package ejektaflex.makkit.client

import ejektaflex.makkit.client.editor.EditRegion
import ejektaflex.makkit.client.editor.input.InputState
import ejektaflex.makkit.client.editor.input.KeyStateHandler
import ejektaflex.makkit.client.event.Events
import ejektaflex.makkit.client.keys.KeyRemapper
import ejektaflex.makkit.client.network.EditIntentPacket
import ejektaflex.makkit.common.world.WorldOperation
import ejektaflex.makkit.client.render.RenderHelper
import ejektaflex.makkit.common.network.EditIntentPacketHandler
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.InputUtil
import net.minecraft.util.Identifier
import org.lwjgl.glfw.GLFW

class MakkitClient : ClientModInitializer {


    val region = EditRegion().apply {
        moveTo(4, 4, 4, 4, 3, 2)
    }

    override fun onInitializeClient() {

        println("Hello Fabric world!")

        ClientSidePacketRegistry.INSTANCE.register(EditIntentPacket.ID) { c, b ->
            EditIntentPacketHandler.run(c, b)
        }

        KeyBindingRegistry.INSTANCE.apply {
            addCategory("KEdit")
            for (bindHandler in keyHandlers) {
                register(bindHandler.binding)
            }
        }

        fillBinding.setKeyDown {
            region.doOperation(WorldOperation.FILL)
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
                        Identifier("kedit", "fill"),
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
                toggleBackBinding
        )


    }


}