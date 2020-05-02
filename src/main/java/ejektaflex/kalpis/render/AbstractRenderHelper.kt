package ejektaflex.kalpis.render

import ejektaflex.kalpis.common.ext.drawOffset
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.render.BufferBuilderStorage
import net.minecraft.client.render.Camera
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Matrix4f

abstract class AbstractRenderHelper {

    protected val mc = MinecraftClient.getInstance()

    val textRenderer: TextRenderer
        get() = mc.textRenderer

    lateinit var matrices: MatrixStack

    protected var tickDelta: Float = 0f

    protected lateinit var camera: Camera

    protected lateinit var buffers: BufferBuilderStorage

    protected lateinit var matrix4f: Matrix4f

    val eVerts: VertexConsumerProvider.Immediate
        get() = buffers.entityVertexConsumers

    fun setState(matricesIn: MatrixStack, tickDeltaIn: Float, cameraIn: Camera, buffersIn: BufferBuilderStorage, matrixIn: Matrix4f) {
        matrices = matricesIn
        tickDelta = tickDeltaIn
        camera = cameraIn
        buffers = buffersIn
        matrix4f = matrixIn
    }


    fun drawInWorld(func: RenderHelper.() -> Unit) {
        matrices.drawOffset(camera.pos, func, RenderHelper)
    }

}