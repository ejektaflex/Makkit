package ejektaflex.kalpis

import ejektaflex.kalpis.data.EditRegion
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

    fun onDrawScreen(e: Events.DrawScreenEvent) {
        // RenderHelper state
        RenderHelper.setState(e.matrices, e.tickDelta, e.camera, e.buffers)

        //val box = Box(1.0, 1.0, 1.0, 2.0, 2.0, 2.0)


        var box = Box(5.0, 5.0, 5.0, 1.0, 1.0, 1.0)

        val region = EditRegion()

        region.pos = region.pos.add(5, 5, 5)
        region.size = region.size.add(4, 1, 2)

        RenderHelper.drawInWorld {

            //println("Hai")

            region.draw(red)

            val result = region.trace()

            if (result != null) {
                val nearest = region.closestBlock(result.hit)
                nearest?.let {
                    drawBox(region.closestOutsidePos(result), BlockPos(1, 1, 1), orange)
                }
            }




        }
    }




}