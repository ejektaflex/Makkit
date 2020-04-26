package ejektaflex.kalpis.render

import ejektaflex.kalpis.ext.drawOffset
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.BufferBuilderStorage
import net.minecraft.client.render.Camera
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.BlockPos

abstract class AbstractRenderHelper {

    protected lateinit var matrices: MatrixStack

    protected var tickDelta: Float = 0f

    protected lateinit var camera: Camera

    protected lateinit var buffers: BufferBuilderStorage

    protected val eVerts: VertexConsumerProvider.Immediate
        get() = buffers.entityVertexConsumers

    fun setState(matricesIn: MatrixStack, tickDeltaIn: Float, cameraIn: Camera, buffersIn: BufferBuilderStorage) {
        matrices = matricesIn
        tickDelta = tickDeltaIn
        camera = cameraIn
        buffers = buffersIn
    }

    protected val mc = MinecraftClient.getInstance()

    fun drawInWorld(func: RenderHelper.() -> Unit) {
        matrices.drawOffset(camera.pos, func, RenderHelper)
    }

    companion object {
        val BLOCK_UNIT = BlockPos(1, 1, 1)
    }

}