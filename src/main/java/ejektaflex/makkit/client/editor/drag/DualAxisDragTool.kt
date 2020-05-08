package ejektaflex.makkit.client.editor.drag

import ejektaflex.makkit.client.MakkitClient
import ejektaflex.makkit.client.data.BoxTraceResult
import ejektaflex.makkit.client.editor.EditRegion
import ejektaflex.makkit.client.editor.input.KeyStateHandler
import ejektaflex.makkit.common.ext.flipMask
import ejektaflex.makkit.client.render.RenderBox
import ejektaflex.makkit.client.render.RenderColor
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

internal abstract class DualAxisDragTool(region: EditRegion, binding: KeyStateHandler) : DragTool(region, binding) {

    protected val plane = RenderBox()

    override fun onStartDragging(start: BoxTraceResult) {
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

    override fun onDraw() {
        if (isDragging()) {
            region.preview.box = calcDragBox(!MakkitClient.config.gridSnapping) ?: region.preview.box
            if (region.drawDragPlane) {
                plane.draw(RenderColor.PINK.toAlpha(0.2f))
            }
        }
    }

}