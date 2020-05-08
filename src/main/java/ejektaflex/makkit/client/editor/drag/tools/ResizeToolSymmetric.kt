package ejektaflex.makkit.client.editor.drag.tools

import ejektaflex.makkit.client.editor.EditRegion
import ejektaflex.makkit.client.editor.drag.SingleAxisDragTool
import ejektaflex.makkit.client.editor.input.KeyStateHandler
import ejektaflex.makkit.common.ext.*
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import kotlin.math.roundToInt

internal class ResizeToolSymmetric (
        region: EditRegion,
        binding: KeyStateHandler
) : SingleAxisDragTool(region, binding) {

    override fun calcDragBox(smooth: Boolean): Box? {
        if (!isDragging()) {
            return null
        }

        val offsets = planes.mapNotNull {
            getDrawOffset(it.box)
        }

        if (offsets.isEmpty()) {
            return null
        }

        val offsetToUse = offsets.minBy { it.distanceTo(dragStart.source) }!!
        val roundedOffset = when (smooth) {
            true -> offsetToUse
            false -> offsetToUse.round()
        }

        // this locks to an axis and flips so that "positive" is in the direction direction
        val change = roundedOffset.multiply(dragStart.dir.vec3d())

        val box = region.area.box
        return Box(box.getStart().subtract(change), box.getEnd().add(change))
    }

    override fun onDraw() {
        super.onDraw()
        region.preview.draw()

        region.preview.drawTextOn(
                dragStart.dir,
                region.preview.box.sizeInDirection(dragStart.dir).roundToInt().toString()
        )
    }

}