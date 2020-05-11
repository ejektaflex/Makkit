package ejektaflex.makkit.client.editor.drag

import ejektaflex.makkit.client.data.BoxTraceResult
import ejektaflex.makkit.client.editor.EditRegion
import ejektaflex.makkit.client.editor.input.KeyStateHandler
import ejektaflex.makkit.client.render.RenderBox
import ejektaflex.makkit.client.render.RenderColor
import ejektaflex.makkit.client.render.RenderHelper
import ejektaflex.makkit.common.ext.*
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import kotlin.math.roundToInt

internal abstract class SingleAxisDragTool(region: EditRegion, binding: KeyStateHandler) : DragTool(region, binding) {

    protected val planeAxis1 = RenderBox()
    protected val planeAxis2 = RenderBox()

    protected val planes: List<RenderBox>
        get() = listOf(planeAxis1, planeAxis2)

    override fun getCursorOffset(snapped: Boolean): Vec3d? {
        val offsets = planes.mapNotNull {
            val current = RenderHelper.boxTrace(it.box, 100f)
            if (current != BoxTraceResult.EMPTY) {
                current.hit.subtract(dragStart.hit)
            } else {
                null
            }
        }

        //println(offsets.toString())

        return (offsets.minBy { it.distanceTo(dragStart.source) } ?: return null).snapped(snapped)
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

    override fun onDrawPreview(offset: Vec3d) {
        preview.box = calcSelectionBox(offset, region.area.box)

        preview.draw()
        preview.drawTextOn(dragStart.dir, preview.box.sizeOnAxis(dragStart.dir.axis).roundToInt().toString())
        if (region.drawDragPlane) {
            planeAxis1.draw(RenderColor.PINK.toAlpha(0.2f))
            planeAxis2.draw(RenderColor.PINK.toAlpha(0.2f))
        }

    }

}