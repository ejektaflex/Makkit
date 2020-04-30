package ejektaflex.kalpis.render

import ejektaflex.kalpis.data.BoxTraceResult
import ejektaflex.kalpis.ext.plus
import ejektaflex.kalpis.ext.rayTraceForSide
import ejektaflex.kalpis.mixin.TextRendererMixin
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.WorldRenderer
import net.minecraft.text.LiteralText
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d

object RenderHelper : AbstractRenderHelper() {

    fun drawBox(pos: BlockPos, size: BlockPos, color: RenderColor) {
        drawBox(Box(pos, pos + size), color)
    }


    fun drawText(pos: Vec3d, text: String, textSize: Float = 1f, center: Boolean = true) {
        matrices.push()
        matrices.translate(pos.x, pos.y, pos.z)
        matrices.multiply(camera.rotation)
        matrices.scale(-textSize/32, -textSize/32, -textSize/32)

        val centerDiv = if (center) 2 else 1

        textRenderer.draw(
                LiteralText(text),
                // x offset to center text
                -textRenderer.getStringWidth(LiteralText(text)).toFloat() / 2,
                -(textRenderer as TextRendererMixin).fontHeight.toFloat() / centerDiv,
                0xFFFFFF,
                false,
                matrices.peek().model,
                buffers.outlineVertexConsumers,
                true, // see through
                0,
                15728880
        )
        matrices.pop()
    }

    fun getLookVector(): Vec3d {
        return mc.player!!.getRotationVec(tickDelta)
    }

    fun getLookDirections(): List<Direction> {
        val vec = getLookVector()
        var dirs = mutableListOf<Direction>()

        if (vec.z < 0) {
            dirs.add(Direction.SOUTH)
        } else {
            dirs.add(Direction.NORTH)
        }

        // Y dir is flipped
        if (vec.y < 0) {
            dirs.add(Direction.UP)
        } else {
            dirs.add(Direction.DOWN)
        }

        if (vec.x < 0) {
            dirs.add(Direction.EAST)
        } else {
            dirs.add(Direction.WEST)
        }

        return dirs
    }

    fun drawBox(box: Box, color: RenderColor = RenderColor.WHITE) {
        val colors = color.floats
        WorldRenderer.drawBox(
                matrices,
                eVerts.getBuffer(RenderLayer.getLines()),
                box,
                colors[0], colors[1], colors[2], colors[3]
        )
    }


    fun boxTraceForSide(box: Box, distance: Float = mc.interactionManager!!.reachDistance * 6): BoxTraceResult? {
        val player = mc.player!!
        val vec1 = player.getCameraPosVec(tickDelta)
        val vec2 = player.getRotationVec(tickDelta)
        return box.rayTraceForSide(
                vec1, vec1.add(vec2.x * distance, vec2.y * distance, vec2.z * distance)
        )
    }


}