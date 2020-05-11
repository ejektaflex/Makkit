package ejektaflex.makkit.client.editor.drag

import ejektaflex.makkit.client.MakkitClient
import ejektaflex.makkit.client.data.BoxTraceResult
import ejektaflex.makkit.client.editor.EditRegion
import ejektaflex.makkit.client.editor.input.KeyStateHandler
import ejektaflex.makkit.common.ext.flipMask
import ejektaflex.makkit.client.render.RenderBox
import ejektaflex.makkit.client.render.RenderColor
import ejektaflex.makkit.client.render.RenderHelper
import ejektaflex.makkit.common.ext.getStart
import ejektaflex.makkit.common.ext.snapped
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

internal abstract class DualAxisDragTool(region: EditRegion, binding: KeyStateHandler) : DragTool(region, binding) {

    private val plane = RenderBox()

    override fun getDrawOffset(snapped: Boolean): Vec3d? {
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

    override fun onDrawPreview(offset: Vec3d) {
        preview.box = calcSelectionBox(offset)
        if (region.drawDragPlane) {
            plane.draw(RenderColor.PINK.toAlpha(0.2f))
        }
    }

}