package ejektaflex.testmod

import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import org.lwjgl.opengl.GL11
import java.util.*

object RenderHelper {

    private val mc = MinecraftClient.getInstance()

    private fun MatrixStack.withOffset(pos: Vec3d, func: () -> Unit) {
        translate(-pos.x, -pos.y, -pos.z)
        func()
        translate(pos.x, pos.y, pos.z)
    }


    private fun makeBox(pos: Vec3d, size: Vec3d): Box {
        return Box(
                pos.x, pos.y, pos.z,
                pos.x + size.x, pos.y + size.y, pos.z + size.z
        )
    }

    fun drawBox(matrices: MatrixStack, camera: Camera, buffers: BufferBuilderStorage) {
        matrices.withOffset(camera.pos) {
            WorldRenderer.drawBox(
                    matrices,
                    buffers.entityVertexConsumers.getBuffer(MyLayers.OVERLAY_LINES),
                    makeBox(
                            Vec3d(3.0, 3.0, 3.0),
                            Vec3d(2.0, 2.0, 2.0)
                    ),
                    1f, 1f, 1f, 1f
            )
        }
    }


    @Suppress("INACCESSIBLE_TYPE")
    class MyLayers(name: String?, format: VertexFormat?, p_i225992_3_: Int, p_i225992_4_: Int, p_i225992_5_: Boolean, p_i225992_6_: Boolean, runnablePre: Runnable?, runnablePost: Runnable?) : RenderLayer(name, format, p_i225992_3_, p_i225992_4_, p_i225992_5_, p_i225992_6_, runnablePre, runnablePost) {
        companion object {
            val OVERLAY_LINES: RenderLayer = of("overlay_lines",
                    VertexFormats.POSITION_COLOR, GL11.GL_LINES, 256,
                    MultiPhaseParameters.builder()
                            .lineWidth(LineWidth(OptionalDouble.of(3.0)))
                            .transparency(TRANSLUCENT_TRANSPARENCY)
                            .texture(NO_TEXTURE)
                            .depthTest(DepthTest(519))
                            .cull(Cull(false))
                            .lightmap(Lightmap(false))
                            .writeMaskState(WriteMaskState(true, false))
                            .build(false))
        }
    }


}