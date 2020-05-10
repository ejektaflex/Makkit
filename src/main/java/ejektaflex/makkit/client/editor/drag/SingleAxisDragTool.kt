package ejektaflex.makkit.client.editor.drag

import ejektaflex.makkit.client.MakkitClient
import ejektaflex.makkit.client.data.BoxTraceResult
import ejektaflex.makkit.client.editor.EditRegion
import ejektaflex.makkit.client.editor.input.KeyStateHandler
import ejektaflex.makkit.common.ext.flipMask
import ejektaflex.makkit.common.ext.otherDirectionalAxes
import ejektaflex.makkit.client.render.RenderBox
import ejektaflex.makkit.client.render.RenderColor
import ejektaflex.makkit.common.ext.round
import ejektaflex.makkit.common.ext.sizeInDirection
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import kotlin.math.roundToInt

internal abstract class SingleAxisDragTool(region: EditRegion, binding: KeyStateHandler) : DragTool(region, binding) {

    private val planeAxis1 = RenderBox()
    private val planeAxis2 = RenderBox()

    val planes: List<RenderBox>
        get() = listOf(planeAxis1, planeAxis2)

    fun nearestPlaneOffset(smoothing: Boolean): Vec3d? {
        val offsets = planes.mapNotNull {
            getDrawOffset(it.box)
        }

        val offsetToUse = offsets.minBy { it.distanceTo(dragStart.source) } ?: return null

        return when (smoothing) {
            true -> offsetToUse
            false -> offsetToUse.round()
        }
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

    override fun onDraw() {
        if (isDragging()) {
            region.preview.box = calcDragBox(!MakkitClient.config.gridSnapping) ?: region.preview.box
            if (region.drawDragPlane) {
                planeAxis1.draw(RenderColor.PINK.toAlpha(0.2f))
                planeAxis2.draw(RenderColor.PINK.toAlpha(0.2f))
            }
        }

        region.preview.draw()

        region.preview.drawTextOn(
                dragStart!!.dir,
                region.preview.box.sizeInDirection(dragStart.dir).roundToInt().toString()
        )
    }

}