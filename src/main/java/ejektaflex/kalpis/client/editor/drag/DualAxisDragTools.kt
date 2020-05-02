package ejektaflex.kalpis.client.editor.drag

import ejektaflex.kalpis.client.data.BoxTraceResult
import ejektaflex.kalpis.client.editor.EditRegion
import ejektaflex.kalpis.client.editor.input.KeyStateHandler
import ejektaflex.kalpis.common.ext.flipMask
import ejektaflex.kalpis.render.RenderBox
import ejektaflex.kalpis.render.RenderColor
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
            region.preview.box = calcDragBox(region.smoothDrag) ?: region.preview.box

            if (region.drawDragPlane) {
                plane.draw(RenderColor.PINK.toAlpha(0.2f))
            }
        }
    }

}