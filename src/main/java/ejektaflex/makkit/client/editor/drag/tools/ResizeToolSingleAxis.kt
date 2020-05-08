package ejektaflex.makkit.client.editor.drag.tools

import ejektaflex.makkit.client.editor.EditRegion
import ejektaflex.makkit.client.editor.drag.SingleAxisDragTool
import ejektaflex.makkit.client.editor.input.KeyStateHandler
import ejektaflex.makkit.common.ext.*
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import kotlin.math.roundToInt

internal class ResizeToolSingleAxis(
        region: EditRegion,
        binding: KeyStateHandler,
        val opposite: Boolean = false
) : SingleAxisDragTool(region, binding) {

    // Constrain to direction
    override fun getDrawOffset(box: Box): Vec3d? {
        return super.getDrawOffset(box)?.dirMask(dragStart!!.dir)
    }

    override fun onDraw() {
        super.onDraw()
        region.preview.draw()

        region.preview.drawTextOn(
                dragStart!!.dir,
                region.preview.box.sizeInDirection(dragStart!!.dir).roundToInt().toString()
        )
    }

    override fun calcDragBox(smooth: Boolean): Box? {
        if (isDragging()) {
            val offsets = planes.mapNotNull {
                getDrawOffset(it.box)
            }

            if (offsets.isNotEmpty()) {

                // Only use the offset of the closer of the two planes
                val offsetToUse = offsets.minBy { it.distanceTo(dragStart!!.source) }!!

                val rounding = when (smooth) {
                    true -> offsetToUse
                    false -> offsetToUse.round()
                }

                val shrinkVec = rounding.multiply(dragStart!!.dir.opposite.vec3d())
                val dir = dragStart!!.dir

                return when (opposite) {
                    false -> {
                        region.area.box.shrinkSide(shrinkVec, dir)
                    }
                    true -> {
                        region.area.box.shrinkSide(shrinkVec, dir.opposite)
                    }
                }
            }
        }

        return null
    }

}