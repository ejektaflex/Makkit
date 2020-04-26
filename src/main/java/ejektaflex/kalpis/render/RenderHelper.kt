package ejektaflex.kalpis.render

import ejektaflex.kalpis.ext.BoxTraceResult
import ejektaflex.kalpis.ext.plus
import ejektaflex.kalpis.ext.rayTraceForSide
import net.minecraft.client.render.WorldRenderer
import net.minecraft.client.util.math.Vector3f
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction

object RenderHelper : AbstractRenderHelper() {

    fun drawBox(pos: BlockPos, size: BlockPos, color: Vector3f = Vector3f(1f, 1f, 1f)) {
        drawBox(Box(pos, pos + size), color)
    }

    val red = Vector3f(1f, 0f, 0f)
    val orange = Vector3f(1f, 0.5f, 0f)

    fun drawBox(box: Box, color: Vector3f = Vector3f(1f, 1f, 1f)) {
        WorldRenderer.drawBox(
                matrices,
                eVerts.getBuffer(MyLayers.OVERLAY_LINES),
                box,
                color.x, color.y, color.z, 1f
        )
    }


    fun boxTraceForSide(box: Box, distance: Float = mc.interactionManager!!.reachDistance): BoxTraceResult? {
        val player = mc.player!!
        val vec1 = player.getCameraPosVec(tickDelta)
        val vec2 = player.getRotationVec(tickDelta)
        return box.rayTraceForSide(
                vec1, vec1.add(vec2.x * distance, vec2.y * distance, vec2.z * distance)
        )
    }

    fun drawBoxBox(pos: BlockPos, size: BlockPos) {



    }

    fun drawBoxWall(start: BlockPos, size: Pair<Int, Int>, dir1: Direction, dir2: Direction)  {

    }

}