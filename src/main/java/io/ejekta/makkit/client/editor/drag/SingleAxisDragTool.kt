package io.ejekta.makkit.client.editor.drag

import io.ejekta.makkit.client.data.BoxTraceResult
import io.ejekta.makkit.client.editor.EditRegion
import io.ejekta.makkit.client.render.RenderColor
import io.ejekta.makkit.client.render.RenderHelper
import io.ejekta.makkit.common.ext.*
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

internal abstract class SingleAxisDragTool(ctx: EditRegion.HandleContext) : DragTool(ctx) {

    protected var planeAxis1: Box = EMPTY_BOX
    protected var planeAxis2: Box = EMPTY_BOX

    protected val planes: List<Box>
        get() = listOf(planeAxis1, planeAxis2)

    init {
        println("Created $this and it has planes: $planes $planeAxis1")
    }

    override fun getCursorOffset(snapped: Boolean): Vec3d {
        println("PLANES: $planes")
        val offsets = planes.mapNotNull {
            println("PLANE: $it")
            val current = RenderHelper.boxTrace(it, 1000f)
            if (current != BoxTraceResult.EMPTY) {
                current
            } else {
                null
            }
        }
        return (offsets.minByOrNull {
            it.hit.distanceTo(it.source)
        } ?: return Vec3d.ZERO).hit.subtract(dragStart.hit).snapped(snapped)
    }

    override fun onStartDragging(start: BoxTraceResult) {
        super.onStartDragging(start)

        val renderPlanes = planes.toMutableList()

        val dirs = start.dir.otherDirectionsSameSigNum()

        dirs.forEachIndexed { i, direction ->
            println("Start dragging set render plane: $i")
            val areaSize = Vec3d(
                    DRAG_PLANE_SIZE,
                    DRAG_PLANE_SIZE,
                    DRAG_PLANE_SIZE
            ).flatMasked(direction)
            renderPlanes[i] = Box(
                    start.hit.subtract(areaSize),
                    start.hit.add(areaSize)
            )
            println("Was set to: ${renderPlanes[i]}")
        }
        println("Planes for setting: $renderPlanes")
        planeAxis1 = renderPlanes[0]
        planeAxis2 = renderPlanes[1]
    }

    override fun getSelectionBox(offset: Vec3d, oldSelection: Box, preview: Box): Box {
        return preview
    }

    override fun onDrawPreview(offset: Vec3d) {
        super.onDrawPreview(offset)

        if (region.drawDragPlane) {
            planeAxis1.draw(RenderColor.PINK.toAlpha(0.2f))
            planeAxis2.draw(RenderColor.PINK.toAlpha(0.2f))
        }
    }

}