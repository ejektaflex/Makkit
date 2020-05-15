package io.ejekta.makkit.client.editor.drag

import io.ejekta.makkit.client.data.BoxTraceResult
import io.ejekta.makkit.client.editor.EditRegion
import io.ejekta.makkit.client.editor.input.KeyStateHandler
import io.ejekta.makkit.common.ext.flipMask
import io.ejekta.makkit.client.render.RenderBox
import io.ejekta.makkit.client.render.RenderColor
import io.ejekta.makkit.client.render.RenderHelper
import io.ejekta.makkit.common.ext.snapped
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

internal abstract class DualAxisDragTool(region: EditRegion, binding: KeyStateHandler) : DragTool(region, binding) {

    private val plane = RenderBox()

    override fun getCursorOffset(snapped: Boolean): Vec3d? {
        val current = RenderHelper.boxTrace(plane.box)
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
        ).flipMask(start.dir)

        plane.box = Box(
                start.hit.subtract(areaSize),
                start.hit.add(areaSize)
        )
    }

    override fun getSelectionBox(offset: Vec3d, oldSelection: Box, preview: Box): Box {
        return preview
    }

    override fun onDrawPreview(offset: Vec3d) {
        preview.box = getPreviewBox(offset, region.selection)
        if (region.drawDragPlane) {
            plane.draw(RenderColor.PINK.toAlpha(0.2f))
        }
    }

}