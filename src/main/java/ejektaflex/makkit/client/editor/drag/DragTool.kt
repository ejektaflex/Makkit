package ejektaflex.makkit.client.editor.drag

import ejektaflex.makkit.client.data.BoxTraceResult
import ejektaflex.makkit.client.editor.EditRegion
import ejektaflex.makkit.client.editor.IEditor
import ejektaflex.makkit.client.editor.input.InputState
import ejektaflex.makkit.client.editor.input.KeyStateHandler
import ejektaflex.makkit.client.render.RenderHelper
import ejektaflex.makkit.common.ext.getEnd
import ejektaflex.makkit.common.ext.getStart
import ejektaflex.makkit.common.network.pakkits.server.BoxMovementLocalUpdate
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

internal abstract class DragTool(val region: EditRegion, val keyHandler: KeyStateHandler) : IEditor {

    var dragStart: BoxTraceResult? = null

    fun isDragging(): Boolean {
        return dragStart != null
    }

    override fun shouldDraw(): Boolean {
        return isDragging()
    }

    abstract fun onStartDragging(start: BoxTraceResult)

    abstract fun calcDragBox(smooth: Boolean): Box?

    open fun onStopDragging(stop: BoxTraceResult) {
        val box = calcDragBox(false)
        box?.let {
            region.area.box = it
            BoxMovementLocalUpdate(
                    BlockPos(it.getStart()),
                    BlockPos(it.getEnd())
            ).sendToServer()
        }
    }

    override fun update() {
        // Try to start dragging
        if (dragStart == null && keyHandler.isDown) {
            dragStart = region.area.trace(reverse = InputState.isBackSelecting)
            if (dragStart != null) {
                onStartDragging(dragStart!!)
            }
        }

        // Try to stop dragging
        if (dragStart != null && !keyHandler.isDown) {
            onStopDragging(dragStart!!)
            dragStart = null
        }

    }

    open fun getDrawOffset(box: Box): Vec3d? {
        val current = RenderHelper.boxTraceForSide(box)
        if (dragStart != null && current != null) {
            return current.hit.subtract(dragStart!!.hit)
        }
        return null
    }

}
