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

internal abstract class DualAxisDragTools(region: EditRegion, binding: KeyStateHandler) : DragTool(region, binding) {

    protected val plane = RenderBox()

    override fun onStartDragging(start: BoxTraceResult) {
        println("Dragging move")
        val areaSize = Vec3d(
                region.samplePlaneSize,
                region.samplePlaneSize,
                region.samplePlaneSize
        ).flipMask(start.dir)

        plane.box = Box(
                start.hit.subtract(areaSize),
                start.hit.add(areaSize)
        )
    }

    override fun onStopDragging(stop: BoxTraceResult) {
        val box = calcDragBox(false)
        box?.let { region.area.box = it }
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