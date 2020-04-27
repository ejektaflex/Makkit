package ejektaflex.kalpis.edit

import ejektaflex.kalpis.ExampleMod
import ejektaflex.kalpis.data.BoxTraceResult
import ejektaflex.kalpis.ext.dirMask
import ejektaflex.kalpis.render.RenderBox
import ejektaflex.kalpis.render.RenderColor
import ejektaflex.kalpis.render.RenderHelper
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

class EditRegion() {

    val region = BlockRegion()

    val blocksRender = RenderBox()

    val dragRender = RenderBox()

    fun moveTo(x: Int, y: Int, z: Int, sx: Int, sy: Int, sz: Int) {
        region.pos = BlockPos(x, y, z)
        region.size = BlockPos(x + sx, y + sy, z + sz)
    }

    var wasDragging = false
    var isDragging = false

    var dragHit: BoxTraceResult? = null

    fun onRelease() {

    }

    fun onGrab() {

    }

    fun update() {
        blocksRender.fitTo(region)
        val isDragging = ExampleMod.dragBinding.isPressed


    }

    val movePlane = RenderBox()

    fun draw() {

        val result = blocksRender.trace()

        blocksRender.color = if (result != null) {
            RenderColor.GREEN
        } else {
            RenderColor.RED
        }








        blocksRender.draw()
        dragRender.draw(RenderColor.ORANGE)

        // 7.37, 6.77, 4.0
        // north on 0, 0, 1

        // Need: 2.37, 1.77, 4.0 <-> 12.37, 11.77, 4.0

        // Hitmask: 7.37, 6.77, 0.0
        // 5/5/5 mask: 1/1/0 * 5/5/5 = 5/5/0

        if (result != null) {

            val areaSize = Vec3d(1.0, 1.0, 1.0).dirMask(result.dir)

            movePlane.box = Box(
                    result.hit.subtract(areaSize),
                    result.hit.add(areaSize)
            )

            

        }

        movePlane.draw(RenderColor.BLUE)


        /*
        if (result != null) {
            val nearest = region.closestBlock(result.hit)
            nearest?.let {
                RenderHelper.drawBox(region.closestOutsidePos(result), BlockPos(1, 1, 1), RenderColor.ORANGE)
            }
        }
        */
    }




}