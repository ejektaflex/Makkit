package ejektaflex.kalpis.render

import ejektaflex.kalpis.data.BoxTraceResult
import ejektaflex.kalpis.ext.plus
import ejektaflex.kalpis.ext.rayTraceForSide
import net.minecraft.client.render.WorldRenderer
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction

object RenderHelper : AbstractRenderHelper() {

    fun drawBox(pos: BlockPos, size: BlockPos, color: RenderColor) {
        drawBox(Box(pos, pos + size), color)
    }





    fun drawBox(box: Box, color: RenderColor = RenderColor.WHITE) {
        val colors = color.floats
        WorldRenderer.drawBox(
                matrices,
                eVerts.getBuffer(MyLayers.OVERLAY_LINES),
                box,
                colors[0], colors[1], colors[2], colors[3]
        )
    }


    fun boxTraceForSide(box: Box, distance: Float = mc.interactionManager!!.reachDistance * 3): BoxTraceResult? {
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