package ejektaflex.kalpis.edit

import ejektaflex.kalpis.data.BoxTraceResult
import ejektaflex.kalpis.edit.drag.Drag
import ejektaflex.kalpis.edit.planes.MovePlane
import ejektaflex.kalpis.ext.dirMask
import ejektaflex.kalpis.ext.round
import ejektaflex.kalpis.render.RenderBox
import ejektaflex.kalpis.render.RenderColor
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

class EditRegion() {

    val region = BlockRegion()

    val blocksRender = RenderBox()

    val previewSmooth = RenderBox()
    val previewBlocky = RenderBox()

    val drag = Drag(this)

    val movePlane = MovePlane(this)

    fun moveTo(x: Int, y: Int, z: Int, sx: Int, sy: Int, sz: Int) {
        region.pos = BlockPos(x, y, z)
        region.size = BlockPos(x + sx, y + sy, z + sz)
    }


    fun update() {
        blocksRender.fitTo(region)
        movePlane.update()

        drag.update()
    }


    fun onStartDragging(dragPoint: BoxTraceResult) {

        val areaSize = Vec3d(16.0, 16.0, 16.0).dirMask(dragPoint.dir)

        movePlane.hitbox.box = Box(
                dragPoint.hit.subtract(areaSize),
                dragPoint.hit.add(areaSize)
        )

    }

    fun onStopDragging(stopPoint: BoxTraceResult) {

    }

    fun draw() {

        blocksRender.color = RenderColor.GREEN

        movePlane.tryDraw()

        val offset = movePlane.getDrawOffset(drag)

        if (offset != null) {
            previewSmooth.fitTo(region)
            previewSmooth.draw(RenderColor.BLUE, offset)
            previewBlocky.fitTo(region)
            previewBlocky.draw(RenderColor.ORANGE, offset.round())
        }

        blocksRender.draw()

    }




}