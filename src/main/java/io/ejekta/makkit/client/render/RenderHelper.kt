package io.ejekta.makkit.client.render

import io.ejekta.makkit.client.MakkitClient
import io.ejekta.makkit.client.data.BoxTraceResult
import io.ejekta.makkit.client.mixin.TextRendererMixin
import io.ejekta.makkit.common.ext.*
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.WorldRenderer
import net.minecraft.text.Text
import net.minecraft.util.math.*
import org.joml.Matrix4f
import kotlin.math.abs
import kotlin.math.sign

object RenderHelper : AbstractRenderHelper() {




    fun drawText(pos: Vec3d, text: String, textSize: Float = 1f, center: Boolean = true) {
        matrices.push()
        matrices.translate(pos.x, pos.y, pos.z)
        matrices.multiply(camera.rotation)
        val newTextSize = -(textSize * MakkitClient.axialTextSize)/32
        matrices.scale(newTextSize, newTextSize, newTextSize)

        val centerDiv = if (center) 2 else 1

        textRenderer.draw(
                Text.literal(text),
                // x offset to center text
                -textRenderer.getWidth(Text.literal(text)).toFloat() / 2,
                -(textRenderer as TextRendererMixin).fontHeight.toFloat() / centerDiv,
                -1,
                false,
                matrices.peek().positionMatrix,
                buffers.outlineVertexConsumers,
                TextRenderer.TextLayerType.SEE_THROUGH,
                0,
            0xF000F0
        )
        matrices.pop()
    }

    fun getLookVector(): Vec3d {
        return mc.player!!.getRotationVec(tickDelta)
    }

    fun getLookCardinalDirection(look: Vec3d = getLookVector()): Direction {
        // we don't care about up or down.
        return if (abs(look.x) > abs(look.z)) {
            Direction.fromVector(sign(look.x).toInt(), 0, 0)!!
        } else {
            Direction.fromVector(0, 0, sign(look.z).toInt())!!
        }
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

    // Single block drawing
    fun drawBlockFaces(pos: BlockPos, color: RenderColor = RenderColor.WHITE, layer: RenderLayer = MyLayers.OVERLAY_QUADS) {
        drawBoxFilled(Box(pos.vec3d(), pos.vec3d().add(1.0, 1.0, 1.0)), color, layer)
    }


    fun drawBoxEdges(
            box: Box,
            color: RenderColor = RenderColor.WHITE,
            layerFront: RenderLayer = MyLayers.OVERLAY_LINES_FRONT,
            layerBack: RenderLayer = MyLayers.OVERLAY_LINES_BEHIND
    ) {
        val colors = color.floats

        matrices.push()

        // In front of ground
        WorldRenderer.drawBox(
                matrices,
                eVerts.getBuffer(layerFront),
                box,
                colors[0], colors[1], colors[2], colors[3]
        )

        // Behind ground
        WorldRenderer.drawBox(
                matrices,
                eVerts.getBuffer(layerBack),
                box,
                colors[0], colors[1], colors[2], colors[3]
        )

        matrices.pop()

    }

    fun drawFaceFilled(box: Box, side: Direction, color: RenderColor, layer: RenderLayer = MyLayers.OVERLAY_QUADS) {
        BoxData.getDrawFunc(side).invoke(
                box.offsetBy(0.0025, side), // offset to avoid z-fighting that happens in prod
                eVerts.getBuffer(layer),
                RenderHelper.matrices.peek().positionMatrix,
                color
        )
    }

    fun drawBoxFilled(box: Box, color: RenderColor, layer: RenderLayer = MyLayers.OVERLAY_QUADS) {
        for (side in enumValues<Direction>()) {
            drawFaceFilled(box, side, color, layer)
        }
    }

    // hax, only use for debugging
    fun drawPoint(point: Vec3d, color: RenderColor = RenderColor.WHITE, size: Double = 0.05) {
        val off = Vec3d(size, size, size)
        drawBoxFilled(
                Box(
                        point.subtract(off),
                        point.add(off)
                ),
                color
        )
    }

    fun drawLine(start: Vec3d, end: Vec3d, color: RenderColor = RenderColor.WHITE, layer: RenderLayer = MyLayers.OVERLAY_LINES_BOTH) {
        val vert = eVerts.getBuffer(layer)
        val posMat = RenderHelper.matrices.peek().positionMatrix
        val normalMat = RenderHelper.matrices.peek().normalMatrix

        vert.vertex(posMat, start.x, start.y, start.z).color(color).normal(normalMat, 0f, 0f, 0f).next()
        vert.vertex(posMat, end.x, end.y, end.z).color(color).normal(normalMat, 0f, 0f, 0f).next()
    }

    fun boxTrace(box: Box, distance: Float = mc.interactionManager?.reachDistance ?: 15f, reverse: Boolean = false): BoxTraceResult {
        val player = mc.player ?: return BoxTraceResult.EMPTY
        // Camera position and rotation
        val vec1 = player.getCameraPosVec(tickDelta)
        val vec2 = player.getRotationVec(tickDelta)
        // Projected position into space
        val vec3 = vec1.add(vec2.x * distance, vec2.y * distance, vec2.z * distance)
        return when (reverse) {
            false -> box.rayTraceForSide(vec1, vec3)
            true -> box.rayTraceForSide(vec3, vec1)
        } ?: BoxTraceResult.EMPTY
    }


    private object BoxData {

        fun getDrawFunc(side: Direction): Box.(vert: VertexConsumer, mat: Matrix4f, color: RenderColor) -> Unit {
            return drawDirections[side] ?: error("Draw Func for direction ${side.name} does not exist!")
        }

        private val drawDown: Box.(vert: VertexConsumer, mat: Matrix4f, color: RenderColor) -> Unit = { vert, mat, color ->
            vert.vertex(mat, maxX, minY, maxZ).color(color).next()
            vert.vertex(mat, minX, minY, maxZ).color(color).next()
            vert.vertex(mat, minX, minY, minZ).color(color).next()
            vert.vertex(mat, maxX, minY, minZ).color(color).next()
        }

        private val drawUp: Box.(vert: VertexConsumer, mat: Matrix4f, color: RenderColor) -> Unit = { vert, mat, color ->
            vert.vertex(mat, minX, maxY, maxZ).color(color).next()
            vert.vertex(mat, maxX, maxY, maxZ).color(color).next()
            vert.vertex(mat, maxX, maxY, minZ).color(color).next()
            vert.vertex(mat, minX, maxY, minZ).color(color).next()
        }

        private val drawNorth: Box.(vert: VertexConsumer, mat: Matrix4f, color: RenderColor) -> Unit = { vert, mat, color ->
            vert.vertex(mat, maxX, minY, minZ).color(color).next()
            vert.vertex(mat, minX, minY, minZ).color(color).next()
            vert.vertex(mat, minX, maxY, minZ).color(color).next()
            vert.vertex(mat, maxX, maxY, minZ).color(color).next()
        }

        private val drawSouth: Box.(vert: VertexConsumer, mat: Matrix4f, color: RenderColor) -> Unit = { vert, mat, color ->
            vert.vertex(mat, minX, minY, maxZ).color(color).next()
            vert.vertex(mat, maxX, minY, maxZ).color(color).next()
            vert.vertex(mat, maxX, maxY, maxZ).color(color).next()
            vert.vertex(mat, minX, maxY, maxZ).color(color).next()
        }

        private val drawWest: Box.(vert: VertexConsumer, mat: Matrix4f, color: RenderColor) -> Unit = { vert, mat, color ->
            vert.vertex(mat, minX, minY, minZ).color(color).next()
            vert.vertex(mat, minX, minY, maxZ).color(color).next()
            vert.vertex(mat, minX, maxY, maxZ).color(color).next()
            vert.vertex(mat, minX, maxY, minZ).color(color).next()
        }

        private val drawEast: Box.(vert: VertexConsumer, mat: Matrix4f, color: RenderColor) -> Unit = { vert, mat, color ->
            vert.vertex(mat, maxX, minY, maxZ).color(color).next()
            vert.vertex(mat, maxX, minY, minZ).color(color).next()
            vert.vertex(mat, maxX, maxY, minZ).color(color).next()
            vert.vertex(mat, maxX, maxY, maxZ).color(color).next()
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