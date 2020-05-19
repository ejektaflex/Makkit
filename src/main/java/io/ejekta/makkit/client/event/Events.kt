package io.ejekta.makkit.client.event

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.client.render.BufferBuilderStorage
import net.minecraft.client.render.Camera
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.ActionResult
import net.minecraft.util.math.Matrix4f

object Events {

    private fun <E : Any> createSimpleEvent(): Event<(E) -> Unit> {
        return EventFactory.createArrayBacked<(E) -> Unit>(Function1::class.java) { listeners ->
            { evt ->
                for (listener in listeners) {
                    listener(evt)
                }
            }
        }
    }

    /**
     * If any listener returns true, this cancels the method
     */
    private fun <E : Any> createCancellableEvent(): Event<(E) -> Boolean> {
        return EventFactory.createArrayBacked<(E) -> Boolean>(Function1::class.java) { listeners ->
            { evt ->
                var cancelled = false
                for (listener in listeners) {
                    cancelled = cancelled || listener(evt)
                }
                cancelled
            }
        }
    }

    data class MouseScrollEvent(val amount: Double) {
        companion object {
            val Dispatcher = createSimpleEvent<MouseScrollEvent>()
        }
    }

    data class MouseClickedEvent(val button: Int) {
        companion object {
            val Dispatcher = createCancellableEvent<MouseClickedEvent>()
        }
    }

    data class DrawScreenEvent(
            val matrices: MatrixStack,
            val tickDelta: Float,
            val camera: Camera,
            val renderer: GameRenderer,
            val buffers: BufferBuilderStorage,
            val matrix: Matrix4f
    ) {
        companion object {
            val Dispatcher = createSimpleEvent<DrawScreenEvent>()
        }
    }





}