package io.ejekta.makkit.client.editor.drag

import io.ejekta.makkit.client.data.BoxTraceResult
import io.ejekta.makkit.client.editor.EditRegion
import io.ejekta.makkit.client.editor.handle.Handle
import io.ejekta.makkit.client.render.RenderColor
import io.ejekta.makkit.client.render.RenderHelper
import io.ejekta.makkit.common.ext.*
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

internal abstract class SingleAxisDragTool(handle: Handle) : DragTool(handle) {

    protected var planeAxis1 = EMPTY_BOX
    protected var planeAxis2 = EMPTY_BOX

    protected val planes: List<Box>
        get() = listOf(planeAxis1, planeAxis2)

    override fun getCursorOffset(snapped: Boolean): Vec3d? {
        val offsets = planes.mapNotNull {
            val current = RenderHelper.boxTrace(it, 1000f)
            if (current != BoxTraceResult.EMPTY) {
                current
            } else {
                null
            }
        }

        return (offsets.minByOrNull {
            it.hit.distanceTo(it.source)
        } ?: return null).hit.subtract(dragStart.hit).snapped(snapped)
    }

    override fun onStartDragging(start: BoxTraceResult) {
        super.onStartDragging(start)

        val renderPlanes = planes.toMutableList()

        val dirs = start.dir.otherDirectionsSameSigNum()

        dirs.forEachIndexed { i, direction ->
            val areaSize = Vec3d(
                    DRAG_PLANE_SIZE,
                    DRAG_PLANE_SIZE,
                    DRAG_PLANE_SIZE
            ).flatMasked(direction)
            renderPlanes[i] = Box(
                    start.hit.subtract(areaSize),
                    start.hit.add(areaSize)
            )
            // TODO why are we setting renderPlanes[i]? Are we using it?
        }
    }

    override fun getSelectionBox(offset: Vec3d, oldSelection: Box, preview: Box): Box {
        return preview
    }

    override fun onDrawPreview(offset: Vec3d) {
        super.onDrawPreview(offset)

        if (region.drawDragPlane) {
            planeAxis1.draw(RenderColor.PINK.toAlpha(0.2f))
            planeAxis2.draw(RenderColor.PINK.toAlpha(0.2f))
        }
    }

}