package ejektaflex.kalpis.render

import ejektaflex.kalpis.ext.plus
import net.minecraft.client.render.WorldRenderer
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction

object RenderHelper : AbstractRenderHelper() {

    fun drawBox(pos: BlockPos, size: BlockPos) {
        WorldRenderer.drawBox(
                matrices,
                eVerts.getBuffer(MyLayers.OVERLAY_LINES),
                Box(pos, pos + size),
                1f, 1f, 1f, 1f
        )
    }

    fun drawBoxBox(pos: BlockPos, size: BlockPos) {



    }

    fun drawBoxWall(start: BlockPos, size: Pair<Int, Int>, dir1: Direction, dir2: Direction)  {

    }

}