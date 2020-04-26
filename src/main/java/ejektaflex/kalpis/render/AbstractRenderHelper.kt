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

    protected lateinit var camera: Camera

    protected lateinit var buffers: BufferBuilderStorage

    protected val eVerts: VertexConsumerProvider.Immediate
        get() = buffers.entityVertexConsumers

    fun setState(matricesIn: MatrixStack, cameraIn: Camera, buffersIn: BufferBuilderStorage) {
        matrices = matricesIn
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