package ejektaflex.testmod.render

import ejektaflex.testmod.ext.drawOffset
import ejektaflex.testmod.ext.plus
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box

object RenderHelper : AbstractRenderHelper() {

    fun drawBox(pos: BlockPos, size: BlockPos) = drawOffset {
        WorldRenderer.drawBox(
                matrices,
                buffers.entityVertexConsumers.getBuffer(MyLayers.OVERLAY_LINES),
                Box(pos, pos + size),
                1f, 1f, 1f, 1f
        )
    }

}