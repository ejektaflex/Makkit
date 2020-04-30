package ejektaflex.kalpis.render

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import org.lwjgl.opengl.GL11
import java.util.*

@Suppress("INACCESSIBLE_TYPE")
class MyLayers(name: String?, format: VertexFormat?, p_i225992_3_: Int, p_i225992_4_: Int, p_i225992_5_: Boolean, p_i225992_6_: Boolean, runnablePre: Runnable?, runnablePost: Runnable?) : RenderLayer(name, format, p_i225992_3_, p_i225992_4_, p_i225992_5_, p_i225992_6_, runnablePre, runnablePost) {
    companion object {

        fun doot() {

            RenderSystem.blendFunc(
                    GlStateManager.SrcFactor.DST_COLOR,
                    GlStateManager.DstFactor.ONE_MINUS_DST_COLOR
            )


        }

        object BehindDepth : DepthTest("overlay_lines_behind", 516)

        object InFrontDepth : DepthTest("overlay_lines_infront", 515)

        object BehindTransparency : Transparency("overlay_lines_trans_ground", {
            RenderSystem.enableBlend()
            doot()
            //RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_COLOR, GlStateManager.DstFactor.ONE)
        }, {
            RenderSystem.disableBlend()
            RenderSystem.defaultBlendFunc()
        })

        private fun commonBuilder(depth: DepthTest, trans: Transparency): MultiPhaseParameters.Builder {
            return MultiPhaseParameters.builder()
                    .lineWidth(LineWidth(OptionalDouble.of(3.0)))
                    .transparency(trans)
                    .texture(NO_TEXTURE)
                    .cull(Cull(false))
                    .depthTest(depth)
                    .lightmap(Lightmap(false))
                    .writeMaskState(WriteMaskState(true, false))
        }

        val OVERLAY_LINES_BEHIND: RenderLayer = of("overlay_lines_behind",
                VertexFormats.POSITION_COLOR, GL11.GL_LINES, 256,
                commonBuilder(BehindDepth, BehindTransparency).build(false))

        val OVERLAY_LINES_FRONT: RenderLayer = of("overlay_lines_front",
                VertexFormats.POSITION_COLOR, GL11.GL_LINES, 256,
                commonBuilder(InFrontDepth, TRANSLUCENT_TRANSPARENCY).build(false))

    }
}