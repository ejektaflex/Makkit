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
        binding: KeyStateHandler
) : SingleAxisDragTool(region, binding) {

    // Constrain to direction
    override fun getDrawOffset(box: Box): Vec3d? {
        return super.getDrawOffset(box)?.dirMask(dragStart.dir)
    }

    override fun calcDragBox(smooth: Boolean): Box? {
        if (isDragging()) {
            val offsets = planes.mapNotNull {
                getDrawOffset(it.box)
            }

            if (offsets.isEmpty()) {
                return null
            }

            // Only use the offset of the closer of the two planes
            val offsetToUse = offsets.minBy { it.distanceTo(dragStart.source) }!!

            val rounding = when (smooth) {
                true -> offsetToUse
                false -> offsetToUse.round()
            }

            val shrinkVec = rounding.multiply(dragStart.dir.opposite.vec3d())
            val dir = dragStart.dir

            return region.area.box.shrinkSide(shrinkVec, dir)
        }
        return null
    }

}