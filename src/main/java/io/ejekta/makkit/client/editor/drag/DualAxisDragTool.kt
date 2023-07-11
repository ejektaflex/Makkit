package io.ejekta.makkit.client.editor.drag

import io.ejekta.makkit.client.data.BoxTraceResult
import io.ejekta.makkit.client.editor.EditRegion
import io.ejekta.makkit.client.editor.handle.Handle
import io.ejekta.makkit.client.render.RenderColor
import io.ejekta.makkit.common.ext.*
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

internal abstract class DualAxisDragTool(handle: Handle) : DragTool(handle) {

    private var plane = EMPTY_BOX

    override fun getCursorOffset(snapped: Boolean): Vec3d? {
        val current = plane.autoTrace()
        return if (current != BoxTraceResult.EMPTY) {
            current.hit.subtract(dragStart.hit)
        } else {
            null
        }?.snapped(snapped)
    }

    override fun onStartDragging(start: BoxTraceResult) {
        super.onStartDragging(start)

        val areaSize = Vec3d(
                DRAG_PLANE_SIZE,
                DRAG_PLANE_SIZE,
                DRAG_PLANE_SIZE
        ).flatMasked(start.dir)

        plane = Box(
                start.hit.subtract(areaSize),
                start.hit.add(areaSize)
        )
    }

    override fun getSelectionBox(offset: Vec3d, oldSelection: Box, preview: Box): Box {
        return preview
    }

    override fun onDrawPreview(offset: Vec3d) {
        super.onDrawPreview(offset)

        if (region.drawDragPlane) {
            plane.draw(RenderColor.PINK.toAlpha(0.2f))
        }
    }

}