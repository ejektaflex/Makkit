package ejektaflex.kalpis.render

import ejektaflex.kalpis.ext.plus
import net.minecraft.client.render.WorldRenderer
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction

object RenderHelper : AbstractRenderHelper() {

    fun drawBox(pos: BlockPos, size: BlockPos) {
        drawBox(Box(pos, pos + size))
    }

    fun drawBox(box: Box) {
        WorldRenderer.drawBox(
                matrices,
                eVerts.getBuffer(MyLayers.OVERLAY_LINES),
                box,
                1f, 1f, 1f, 1f
        )
    }

    fun boxTrace(box: Box, distance: Float = mc.interactionManager!!.reachDistance) {
        camera.updateEyeHeight()
        val player = mc.player!!
        val vec1 = player.getCameraPosVec(tickDelta)
        val vec2 = player.getRotationVec(tickDelta)
        val result = box.rayTrace(
                vec1, vec1.add(vec2.x * distance, vec2.y * distance, vec2.z * distance)
        )
        val answer = result.ifPresent {
            val a = result.get()
            val b = a
            println("Got")
        }
    }

    fun drawBoxBox(pos: BlockPos, size: BlockPos) {



    }

    fun drawBoxWall(start: BlockPos, size: Pair<Int, Int>, dir1: Direction, dir2: Direction)  {

    }

}