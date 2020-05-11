package ejektaflex.makkit.client.editor.drag

import ejektaflex.makkit.client.MakkitClient
import ejektaflex.makkit.client.data.BoxTraceResult
import ejektaflex.makkit.client.editor.EditRegion
import ejektaflex.makkit.client.editor.input.KeyStateHandler
import ejektaflex.makkit.client.render.RenderBox
import ejektaflex.makkit.common.ext.*
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import kotlin.math.roundToInt

internal abstract class SingleAxisDragTool(region: EditRegion, binding: KeyStateHandler) : DragTool(region, binding) {

    private val planeAxis1 = RenderBox()
    private val planeAxis2 = RenderBox()

    val planes: List<RenderBox>
        get() = listOf(planeAxis1, planeAxis2)

    fun nearestPlaneOffset(snap: Boolean): Vec3d? {
        val offsets = planes.mapNotNull {
            getDrawOffset(it.box)
        }

        val offsetToUse = offsets.minBy { it.distanceTo(dragStart.source) } ?: return null

        return offsetToUse.snapped(snap)
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

    override fun onDrawPreview() {
        if (isDragging()) {
            preview.box = calcSelectionBox(MakkitClient.config.gridSnapping) ?: preview.box
        }

        preview.draw()
        preview.drawTextOn(dragStart.dir, preview.box.sizeOnAxis(dragStart.dir.axis).roundToInt().toString())
    }

}