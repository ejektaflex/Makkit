package io.ejekta.makkit.client.render

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.RenderPhase
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormat.DrawMode
import net.minecraft.client.render.VertexFormats
import org.lwjgl.opengl.GL11
import java.util.*

@Suppress("INACCESSIBLE_TYPE")
class MyLayers(
    name: String,
    vertexFormat: VertexFormat,
    drawMode: DrawMode,
    expectedBufferSize: Int,
    hasCrumbling: Boolean,
    translucent: Boolean,
    startAction: Runnable,
    endAction: Runnable
) : RenderLayer(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction) {

    companion object {

        private object BehindDepth : DepthTest("overlay_lines_behind", 516)

        private object InFrontDepth : DepthTest("overlay_lines_infront", 515)

        private object BehindTransparency : Transparency("overlay_lines_trans_ground", {
            RenderSystem.enableBlend()
            RenderSystem.blendFunc(
                    GlStateManager.SrcFactor.DST_COLOR,
                    GlStateManager.DstFactor.ONE_MINUS_DST_COLOR
            )
        }, {
            RenderSystem.disableBlend()
            RenderSystem.defaultBlendFunc()
        })

        private fun commonBuilder(depth: DepthTest, trans: Transparency): MultiPhaseParameters.Builder {
            return MultiPhaseParameters.builder()
                    .lineWidth(LineWidth(OptionalDouble.of(4.0)))
                    .transparency(trans)
                    .texture(NO_TEXTURE)
                    .cull(Cull(false))
                    .depthTest(depth)
                    .lightmap(Lightmap(false))
                    .writeMaskState(WriteMaskState(true, false))
        }

        val OVERLAY_LINES_BEHIND: RenderLayer = of("overlay_lines_behind",
                VertexFormats.POSITION_COLOR, DrawMode.LINES, 256,
                commonBuilder(BehindDepth, BehindTransparency).build(false))


        val OVERLAY_LINES_FRONT: RenderLayer = of("overlay_lines_front",
                VertexFormats.POSITION_COLOR, DrawMode.LINES, 256,
                commonBuilder(InFrontDepth, TRANSLUCENT_TRANSPARENCY).build(false))

        val OVERLAY_NEW_FRONT: RenderLayer = MyLayers("overlay_lines_front_new",
            VertexFormats.POSITION_COLOR, DrawMode.LINES, 256, false, true, {}, {})

        val OVERLAY_LINES_BOTH: RenderLayer = of("overlay_lines_both",
                VertexFormats.POSITION_COLOR, DrawMode.LINES, 256,
                commonBuilder(DepthTest("always", 519), TRANSLUCENT_TRANSPARENCY).build(false))

        val OVERLAY_LINES_HIT_WALL: RenderLayer = of("overlay_lines_both",
                VertexFormats.POSITION_COLOR, DrawMode.LINES, 256,
                commonBuilder(DepthTest("always", 514), TRANSLUCENT_TRANSPARENCY)
                        .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
                        .overlay(RenderPhase.ENABLE_OVERLAY_COLOR)
                        .build(false))

        val OVERLAY_QUADS: RenderLayer = of("overlay_quads",
                VertexFormats.POSITION_COLOR, DrawMode.QUADS, 256,
                MultiPhaseParameters.builder()
                        // Fix Z-Fighting on overlapping planes with ground
                        .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
                        .transparency(TRANSLUCENT_TRANSPARENCY)
                        .writeMaskState(COLOR_MASK)
                        //.shadeModel(SMOOTH_SHADE_MODEL)
                        .build(false))

        val OVERLAY_QUADS_BEHIND: RenderLayer = of("overlay_quads_behind",
                VertexFormats.POSITION_COLOR, DrawMode.QUADS, 256,
                MultiPhaseParameters.builder()
                        .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
                        .transparency(TRANSLUCENT_TRANSPARENCY)
                        .depthTest(DepthTest("overlay_quads_behind", 516))
                        .writeMaskState(COLOR_MASK)
                        //.shadeModel(SMOOTH_SHADE_MODEL)
                        .build(false))

    }
}