package ejektaflex.kalpis.render

import ejektaflex.kalpis.data.BoxTraceResult
import ejektaflex.kalpis.ext.*
import ejektaflex.kalpis.mixin.TextRendererMixin
import ejektaflex.kalpis.render.quads.QuadDrawer
import net.minecraft.client.render.*
import net.minecraft.text.LiteralText
import net.minecraft.util.math.*

object RenderHelper : AbstractRenderHelper() {

    val QUAD_BUFFER: VertexConsumer
        get() = RenderHelper.eVerts.getBuffer(MyLayers.OVERLAY_QUADS)

    val LINE_BUFF_FRONT: VertexConsumer
        get() = eVerts.getBuffer(MyLayers.OVERLAY_LINES_FRONT)

    val LINE_BUFF_BEHIND: VertexConsumer
        get() = eVerts.getBuffer(MyLayers.OVERLAY_LINES_BEHIND)

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

        // Y dir is flipped?
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

    fun drawBlockPos(pos: BlockPos, color: RenderColor = RenderColor.WHITE) {
        val colors = color.floats

        WorldRenderer.drawBox(
                matrices,
                eVerts.getBuffer(MyLayers.OVERLAY_LINES_HIT_WALL),
                pos.toBox(),
                colors[0], colors[1], colors[2], colors[3]
        )

    }

    fun drawBoxEdges(box: Box, color: RenderColor = RenderColor.WHITE) {
        val colors = color.floats

        WorldRenderer.drawBox(
                matrices,
                LINE_BUFF_FRONT,
                box,
                colors[0], colors[1], colors[2], colors[3]
        )

        WorldRenderer.drawBox(
                matrices,
                LINE_BUFF_BEHIND,
                box,
                colors[0], colors[1], colors[2], colors[3]
        )

    }

    fun drawFaceFilled(box: Box, side: Direction, color: RenderColor) {
        BoxData.getDrawFunc(side).invoke(box, QUAD_BUFFER, RenderHelper.matrices.peek().model, color)
    }

    fun drawBoxFilled(box: Box, color: RenderColor) {
        for (side in enumValues<Direction>()) {
            drawFaceFilled(box, side, color)
        }
    }

    fun boxTraceForSide(box: Box, distance: Float = mc.interactionManager!!.reachDistance * 6): BoxTraceResult? {
        val player = mc.player!!
        val vec1 = player.getCameraPosVec(tickDelta)
        val vec2 = player.getRotationVec(tickDelta)
        return box.rayTraceForSide(
                vec1, vec1.add(vec2.x * distance, vec2.y * distance, vec2.z * distance)
        )
    }


    object BoxData {

        fun getDrawFunc(side: Direction): Box.(vert: VertexConsumer, mat: Matrix4f, color: RenderColor) -> Unit {
            return drawDirections[side] ?: error("Draw Func for direction ${side.name} does not exist!")
        }

        private val drawDown: Box.(vert: VertexConsumer, mat: Matrix4f, color: RenderColor) -> Unit = { vert, mat, color ->
            vert.vertex(mat, x2, y1, z2).color(color).next()
            vert.vertex(mat, x1, y1, z2).color(color).next()
            vert.vertex(mat, x1, y1, z1).color(color).next()
            vert.vertex(mat, x2, y1, z1).color(color).next()
        }

        private val drawUp: Box.(vert: VertexConsumer, mat: Matrix4f, color: RenderColor) -> Unit = { vert, mat, color ->
            vert.vertex(mat, x1, y2, z2).color(color).next()
            vert.vertex(mat, x2, y2, z2).color(color).next()
            vert.vertex(mat, x2, y2, z1).color(color).next()
            vert.vertex(mat, x1, y2, z1).color(color).next()
        }

        private val drawNorth: Box.(vert: VertexConsumer, mat: Matrix4f, color: RenderColor) -> Unit = { vert, mat, color ->
            vert.vertex(mat, x2, y1, z1).color(color).next()
            vert.vertex(mat, x1, y1, z1).color(color).next()
            vert.vertex(mat, x1, y2, z1).color(color).next()
            vert.vertex(mat, x2, y2, z1).color(color).next()
        }

        private val drawSouth: Box.(vert: VertexConsumer, mat: Matrix4f, color: RenderColor) -> Unit = { vert, mat, color ->
            vert.vertex(mat, x1, y1, z2).color(color).next()
            vert.vertex(mat, x2, y1, z2).color(color).next()
            vert.vertex(mat, x2, y2, z2).color(color).next()
            vert.vertex(mat, x1, y2, z2).color(color).next()
        }

        private val drawWest: Box.(vert: VertexConsumer, mat: Matrix4f, color: RenderColor) -> Unit = { vert, mat, color ->
            vert.vertex(mat, x1, y1, z1).color(color).next()
            vert.vertex(mat, x1, y1, z2).color(color).next()
            vert.vertex(mat, x1, y2, z2).color(color).next()
            vert.vertex(mat, x1, y2, z1).color(color).next()
        }

        private val drawEast: Box.(vert: VertexConsumer, mat: Matrix4f, color: RenderColor) -> Unit = { vert, mat, color ->
            vert.vertex(mat, x2, y1, z2).color(color).next()
            vert.vertex(mat, x2, y1, z1).color(color).next()
            vert.vertex(mat, x2, y2, z1).color(color).next()
            vert.vertex(mat, x2, y2, z2).color(color).next()
        }

        val drawDirections = mapOf(
                Direction.DOWN to drawDown,
                Direction.UP to drawUp,
                Direction.NORTH to drawNorth,
                Direction.SOUTH to drawSouth,
                Direction.WEST to drawWest,
                Direction.EAST to drawEast
        )

    }


}