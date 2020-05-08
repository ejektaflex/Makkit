package ejektaflex.makkit.client.editor.drag

import ejektaflex.makkit.client.MakkitClient
import ejektaflex.makkit.client.data.BoxTraceResult
import ejektaflex.makkit.client.editor.EditRegion
import ejektaflex.makkit.client.editor.input.KeyStateHandler
import ejektaflex.makkit.common.ext.flipMask
import ejektaflex.makkit.common.ext.otherDirectionalAxes
import ejektaflex.makkit.client.render.RenderBox
import ejektaflex.makkit.client.render.RenderColor
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

internal abstract class SingleAxisDragTool(region: EditRegion, binding: KeyStateHandler) : DragTool(region, binding) {

    protected val planeAxis1 = RenderBox()
    protected val planeAxis2 = RenderBox()

    val planes: List<RenderBox>
        get() = listOf(planeAxis1, planeAxis2)


    override fun onStartDragging(start: BoxTraceResult) {

        val renderPlanes = planes

        val dirs = start.dir.otherDirectionalAxes()

        dirs.forEachIndexed { i, direction ->
            val areaSize = Vec3d(
                    region.samplePlaneSize,
                    region.samplePlaneSize,
                    region.samplePlaneSize
            ).flipMask(direction)
            renderPlanes[i].box = Box(
                    start.hit.subtract(areaSize),
                    start.hit.add(areaSize)
            )
        }
    }

    override fun onDraw() {
        if (isDragging()) {
            region.preview.box = calcDragBox(!MakkitClient.config.gridSnapping) ?: region.preview.box
            if (region.drawDragPlane) {
                planeAxis1.draw(RenderColor.PINK.toAlpha(0.2f))
                planeAxis2.draw(RenderColor.PINK.toAlpha(0.2f))
            }
        }
    }

}