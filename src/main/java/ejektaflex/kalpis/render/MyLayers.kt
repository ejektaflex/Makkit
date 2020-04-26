package ejektaflex.kalpis.render

import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import org.lwjgl.opengl.GL11
import java.util.*

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