package ejektaflex.kalpis.render.quads

import ejektaflex.kalpis.ext.color
import ejektaflex.kalpis.ext.vertex
import ejektaflex.kalpis.render.MyLayers
import ejektaflex.kalpis.render.RenderBox
import ejektaflex.kalpis.render.RenderColor
import ejektaflex.kalpis.render.RenderHelper
import net.minecraft.client.render.BufferBuilder
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Matrix4f
import org.lwjgl.opengl.GL11

object QuadDrawer {

    private val QUAD_BUFFER: VertexConsumer
        get() = RenderHelper.eVerts.getBuffer(MyLayers.OVERLAY_QUADS)

    fun drawSide(box: RenderBox, side: Direction) {
        (drawDirections[side] ?: error("Invalid draw side in Quad Drawer")).invoke(box, QUAD_BUFFER, RenderHelper.matrices.peek().model)
    }

    fun draw(box: RenderBox) {
        for (side in drawDirections.values) {
            side.invoke(box, QUAD_BUFFER, RenderHelper.matrices.peek().model)
        }
    }

    private val drawDown: RenderBox.(vert: VertexConsumer, mat: Matrix4f) -> Unit = { vert, mat ->
        box.apply {
            vert.vertex(mat, x2, y1, z2).color(color).next()
            vert.vertex(mat, x1, y1, z2).color(color).next()
            vert.vertex(mat, x1, y1, z1).color(color).next()
            vert.vertex(mat, x2, y1, z1).color(color).next()
        }
    }

    private val drawUp: RenderBox.(vert: VertexConsumer, mat: Matrix4f) -> Unit = { vert, mat ->
        box.apply {
            vert.vertex(mat, x1, y2, z2).color(color).next()
            vert.vertex(mat, x2, y2, z2).color(color).next()
            vert.vertex(mat, x2, y2, z1).color(color).next()
            vert.vertex(mat, x1, y2, z1).color(color).next()
        }
    }

    private val drawNorth: RenderBox.(vert: VertexConsumer, mat: Matrix4f) -> Unit = { vert, mat ->
        box.apply {
            vert.vertex(mat, x2, y1, z1).color(color).next()
            vert.vertex(mat, x1, y1, z1).color(color).next()
            vert.vertex(mat, x1, y2, z1).color(color).next()
            vert.vertex(mat, x2, y2, z1).color(color).next()
        }
    }

    private val drawSouth: RenderBox.(vert: VertexConsumer, mat: Matrix4f) -> Unit = { vert, mat ->
        box.apply {
            vert.vertex(mat, x1, y1, z2).color(color).next()
            vert.vertex(mat, x2, y1, z2).color(color).next()
            vert.vertex(mat, x2, y2, z2).color(color).next()
            vert.vertex(mat, x1, y2, z2).color(color).next()
        }
    }

    private val drawWest: RenderBox.(vert: VertexConsumer, mat: Matrix4f) -> Unit = { vert, mat ->
        box.apply {
            vert.vertex(mat, x1, y1, z1).color(color).next()
            vert.vertex(mat, x1, y1, z2).color(color).next()
            vert.vertex(mat, x1, y2, z2).color(color).next()
            vert.vertex(mat, x1, y2, z1).color(color).next()
        }
    }

    private val drawEast: RenderBox.(vert: VertexConsumer, mat: Matrix4f) -> Unit = { vert, mat ->
        box.apply {
            vert.vertex(mat, x2, y1, z2).color(color).next()
            vert.vertex(mat, x2, y1, z1).color(color).next()
            vert.vertex(mat, x2, y2, z1).color(color).next()
            vert.vertex(mat, x2, y2, z2).color(color).next()
        }
    }

    private val drawDirections = mapOf(
            Direction.DOWN to drawDown,
            Direction.UP to drawUp,
            Direction.NORTH to drawNorth,
            Direction.SOUTH to drawSouth,
            Direction.WEST to drawWest,
            Direction.EAST to drawEast
    )

}