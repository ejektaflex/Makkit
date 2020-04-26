package ejektaflex.testmod

import ejektaflex.testmod.event.Events
import net.fabricmc.api.ModInitializer
import net.minecraft.client.MinecraftClient

class ExampleMod : ModInitializer {

    val mc = MinecraftClient.getInstance()

    override fun onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        println("Hello Fabric world!")

        Events.DrawScreenEvent.Dispatcher.register { e: Events.DrawScreenEvent ->

            RenderHelper.drawBox(e.matrices, e.camera, e.buffers)


        }

    }





}