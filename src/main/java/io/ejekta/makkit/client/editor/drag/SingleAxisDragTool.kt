package io.ejekta.makkit.client.editor.drag

import io.ejekta.makkit.client.data.BoxTraceResult
import io.ejekta.makkit.client.editor.EditRegion
import io.ejekta.makkit.client.editor.input.KeyStateHandler
import io.ejekta.makkit.client.render.RenderBox
import io.ejekta.makkit.client.render.RenderColor
import io.ejekta.makkit.client.render.RenderHelper
import io.ejekta.makkit.common.ext.*
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

internal abstract class SingleAxisDragTool(region: EditRegion, binding: KeyStateHandler) : DragTool(region, binding) {

    protected val planeAxis1 = RenderBox()
    protected val planeAxis2 = RenderBox()

    protected val planes: List<RenderBox>
        get() = listOf(planeAxis1, planeAxis2)

    override fun getCursorOffset(snapped: Boolean): Vec3d? {
        val offsets = planes.mapNotNull {
            val current = RenderHelper.boxTrace(it.box, 1000f)
            if (current != BoxTraceResult.EMPTY) {
                current
            } else {
                null
            }
        }

        return (offsets.minBy {
            it.hit.distanceTo(it.source)
        } ?: return null).hit.subtract(dragStart.hit).snapped(snapped)
    }

    override fun onStartDragging(start: BoxTraceResult) {

        val renderPlanes = planes

        val dirs = start.dir.otherDirectionalAxes()

        dirs.forEachIndexed { i, direction ->
            val areaSize = Vec3d(
                    DRAG_PLANE_SIZE,
                    DRAG_PLANE_SIZE,
                    DRAG_PLANE_SIZE
            ).flipMask(direction)
            renderPlanes[i].box = Box(
                    start.hit.subtract(areaSize),
                    start.hit.add(areaSize)
            )
        }
    }

    override fun getSelectionBox(offset: Vec3d, oldSelection: Box, preview: Box): Box {
        return preview
    }

    override fun onDrawPreview(offset: Vec3d) {
        preview.box = getPreviewBox(offset, region.selection)

        preview.draw()

        if (region.drawDragPlane) {
            planeAxis1.draw(RenderColor.PINK.toAlpha(0.2f))
            planeAxis2.draw(RenderColor.PINK.toAlpha(0.2f))
        }
    }

}