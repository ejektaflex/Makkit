package ejektaflex.kalpis

import ejektaflex.kalpis.edit.EditRegion
import ejektaflex.kalpis.event.Events
import ejektaflex.kalpis.render.RenderHelper
import net.fabricmc.api.ModInitializer
import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box

class ExampleMod : ModInitializer {

    val mc: MinecraftClient = MinecraftClient.getInstance()

    override fun onInitialize() {

        println("Hello Fabric world!")

        Events.DrawScreenEvent.Dispatcher.register(::onDrawScreen)

    }

    val region = EditRegion().apply {
        moveTo(4, 4, 4, 4, 3, 2)
    }

    private fun onDrawScreen(e: Events.DrawScreenEvent) {
        // RenderHelper state
        RenderHelper.setState(e.matrices, e.tickDelta, e.camera, e.buffers)

        RenderHelper.drawInWorld {
            region.update()
            region.draw()
        }
    }




}