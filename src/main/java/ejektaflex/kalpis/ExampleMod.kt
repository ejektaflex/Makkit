package ejektaflex.kalpis

import ejektaflex.kalpis.edit.EditRegion
import ejektaflex.kalpis.event.Events
import ejektaflex.kalpis.render.RenderHelper
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry
import net.fabricmc.fabric.api.event.client.ClientTickCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.InputUtil
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import org.lwjgl.glfw.GLFW

class ExampleMod : ModInitializer {

    val mc: MinecraftClient = MinecraftClient.getInstance()




    val region = EditRegion().apply {
        moveTo(4, 4, 4, 4, 3, 2)
    }

    override fun onInitialize() {

        println("Hello Fabric world!")

        KeyBindingRegistry.INSTANCE.addCategory("KEdit")
        KeyBindingRegistry.INSTANCE.register(dragBinding)

        Events.DrawScreenEvent.Dispatcher.register(::onDrawScreen)

    }


    private fun onDrawScreen(e: Events.DrawScreenEvent) {
        // RenderHelper state
        RenderHelper.setState(e.matrices, e.tickDelta, e.camera, e.buffers)

        RenderHelper.drawInWorld {
            region.update()
            region.draw()
        }
    }

    companion object {
        val dragBinding = FabricKeyBinding.Builder.create(
                Identifier("kedit", "drag"),
                InputUtil.Type.MOUSE,
                GLFW.GLFW_MOUSE_BUTTON_RIGHT,
                "KEdit"
        ).build()
    }


}