package ejektaflex.kalpis.edit

import ejektaflex.kalpis.render.RenderBox
import ejektaflex.kalpis.render.RenderColor
import ejektaflex.kalpis.render.RenderHelper
import net.minecraft.util.math.BlockPos

class EditRegion() {

    val region = BlockRegion()

    val blocksRender = RenderBox()

    fun moveTo(x: Int, y: Int, z: Int, sx: Int, sy: Int, sz: Int) {
        region.pos = BlockPos(x, y, z)
        region.size = BlockPos(x + sx, y + sy, z + sz)
    }


    fun update() {
        blocksRender.fitTo(region)
    }

    fun draw() {

        val result = blocksRender.trace()

        blocksRender.color = if (result != null) {
            RenderColor.GREEN
        } else {
            RenderColor.RED
        }

        blocksRender.draw()

        if (result != null) {
            val nearest = region.closestBlock(result.hit)
            nearest?.let {
                RenderHelper.drawBox(region.closestOutsidePos(result), BlockPos(1, 1, 1), RenderColor.ORANGE)
            }
        }
    }




}