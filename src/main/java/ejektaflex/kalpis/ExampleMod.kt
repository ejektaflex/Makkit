package ejektaflex.kalpis

import ejektaflex.kalpis.event.Events
import ejektaflex.kalpis.render.RenderHelper
import net.fabricmc.api.ModInitializer
import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.BlockPos

class ExampleMod : ModInitializer {

    val mc: MinecraftClient = MinecraftClient.getInstance()

    override fun onInitialize() {

        println("Hello Fabric world!")

        Events.DrawScreenEvent.Dispatcher.register(::onDrawScreen)

    }

    fun onDrawScreen(e: Events.DrawScreenEvent) {
        // RenderHelper state
        RenderHelper.setState(e.matrices, e.camera, e.buffers)

        //val box = Box(1.0, 1.0, 1.0, 2.0, 2.0, 2.0)

        RenderHelper.drawInWorld {

            drawBox(
                    BlockPos(1, 1, 1),
                    BlockPos(1, 1, 1)
            )

            for (entity in mc.world!!.entities) {
                drawBox(
                        BlockPos(entity.blockPos),
                        BlockPos(1, 1, 1)
                )
            }

        }
    }




}