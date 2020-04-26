package ejektaflex.kalpis.event

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.client.render.BufferBuilderStorage
import net.minecraft.client.render.Camera
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.util.math.MatrixStack

object Events {

    private fun <E : Any> createSimpleEvent(): Event<(E) -> Unit> {
        return EventFactory.createArrayBacked(Function1::class.java) { listeners ->
            { evt ->
                for (listener in listeners) {
                    listener(evt)
                }
            }
        }
    }

    data class DrawScreenEvent(val matrices: MatrixStack, val camera: Camera, val renderer: GameRenderer, val buffers: BufferBuilderStorage) {
        companion object {
            val Dispatcher = createSimpleEvent<DrawScreenEvent>()
        }
    }





}