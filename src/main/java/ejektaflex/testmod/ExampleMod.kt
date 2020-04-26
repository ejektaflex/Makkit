package ejektaflex.testmod

import ejektaflex.testmod.event.Events
import ejektaflex.testmod.render.RenderHelper
import net.fabricmc.api.ModInitializer
import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box

class ExampleMod : ModInitializer {

    val mc = MinecraftClient.getInstance()

    override fun onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        println("Hello Fabric world!")

        Events.DrawScreenEvent.Dispatcher.register { e: Events.DrawScreenEvent ->
            // RenderHelper state
            RenderHelper.setState(e.matrices, e.camera, e.buffers)

            val box = Box(1.0, 1.0, 1.0, 2.0, 2.0, 2.0)

            box.rayTrace()

            RenderHelper.drawBox(
                    BlockPos(1, 1, 1),
                    BlockPos(1, 1, 1)
            )



            for (entity in mc.world!!.entities) {

                RenderHelper.drawBox(
                        BlockPos(entity.blockPos),
                        BlockPos(1, 1, 1)
                )

            }




        }

    }





}