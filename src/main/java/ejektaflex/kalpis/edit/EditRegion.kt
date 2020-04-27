package ejektaflex.kalpis.edit

import ejektaflex.kalpis.ExampleMod
import ejektaflex.kalpis.data.BoxTraceResult
import ejektaflex.kalpis.edit.planes.MovePlane
import ejektaflex.kalpis.ext.dirMask
import ejektaflex.kalpis.render.RenderBox
import ejektaflex.kalpis.render.RenderColor
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

class EditRegion() {

    val region = BlockRegion()

    val blocksRender = RenderBox()

    val dragRender = RenderBox()

    val movePlane = MovePlane(this)

    fun moveTo(x: Int, y: Int, z: Int, sx: Int, sy: Int, sz: Int) {
        region.pos = BlockPos(x, y, z)
        region.size = BlockPos(x + sx, y + sy, z + sz)
    }



    var dragStart: BoxTraceResult? = null

    val isDragging: Boolean
        get() = dragStart != null

    fun update() {
        blocksRender.fitTo(region)
        movePlane.update()

        updateDrag()
    }

    private fun updateDrag() {

        // Try to start dragging
        if (dragStart == null && ExampleMod.dragBinding.isPressed) {
            dragStart = blocksRender.trace()
            if (dragStart != null) {
                onStartDragging(dragStart!!)
            }
        }

        // Try to stop dragging
        if (dragStart != null && !ExampleMod.dragBinding.isPressed) {
            dragStart = null
        }

    }

    private fun onStartDragging(dragPoint: BoxTraceResult) {

        val areaSize = Vec3d(16.0, 16.0, 16.0).dirMask(dragPoint.dir)

        movePlane.hitbox.box = Box(
                dragPoint.hit.subtract(areaSize),
                dragPoint.hit.add(areaSize)
        )

    }

    fun draw() {

        val result = blocksRender.trace()

        blocksRender.color = RenderColor.GREEN

        movePlane.tryDraw()

        val hitPlane = movePlane.tryHit()

        if (dragStart != null && hitPlane != null) {
            blocksRender.draw(offset = hitPlane.hit.subtract(dragStart!!.hit))
        } else {
            blocksRender.draw()
        }





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