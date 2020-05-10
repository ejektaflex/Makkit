package ejektaflex.makkit.client.editor.drag

import ejektaflex.makkit.client.data.BoxTraceResult
import ejektaflex.makkit.client.editor.EditRegion
import ejektaflex.makkit.client.editor.IEditor
import ejektaflex.makkit.client.editor.input.InputState
import ejektaflex.makkit.client.editor.input.KeyStateHandler
import ejektaflex.makkit.client.render.RenderBox
import ejektaflex.makkit.client.render.RenderColor
import ejektaflex.makkit.client.render.RenderHelper
import ejektaflex.makkit.common.ext.getEnd
import ejektaflex.makkit.common.ext.getStart
import ejektaflex.makkit.common.network.pakkits.server.ShadowBoxUpdatePacket
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

internal abstract class DragTool(val region: EditRegion, val keyHandler: KeyStateHandler) : IEditor {

    // We can have other preview boxes and draw them in [onDrawPreview], we just need at least one
    open val preview = RenderBox().apply {
        fillColor = RenderColor.BLUE.toAlpha(.4f)
        edgeColor = RenderColor.ORANGE.toAlpha(.2f)
    }

    var dragStart = BoxTraceResult.EMPTY

    fun isDragging(): Boolean {
        return dragStart != BoxTraceResult.EMPTY
    }

    override fun shouldDraw(): Boolean {
        return isDragging()
    }

    abstract fun calcSelectionBox(smooth: Boolean): Box?

    open fun onStartDragging(start: BoxTraceResult) {
        // Do nothing by default
    }

    open fun onStopDragging(stop: BoxTraceResult) {
        val box = calcSelectionBox(false)
        box?.let {
            region.area.box = it
            ShadowBoxUpdatePacket(
                    BlockPos(it.getStart()),
                    BlockPos(it.getEnd())
            ).sendToServer()
        }
    }

    override fun update() {
        // Try to start dragging
        if (dragStart == BoxTraceResult.EMPTY && keyHandler.isDown) {
            dragStart = region.area.trace(reverse = InputState.isBackSelecting)
            if (dragStart != BoxTraceResult.EMPTY) {
                onStartDragging(dragStart)
            }
        }

        // Try to stop dragging
        if (dragStart != BoxTraceResult.EMPTY && !keyHandler.isDown) {
            onStopDragging(dragStart)
            dragStart = BoxTraceResult.EMPTY
        }
    }

    open fun getDrawOffset(box: Box): Vec3d? {
        val current = RenderHelper.boxTrace(box)
        if (dragStart != BoxTraceResult.EMPTY && current != BoxTraceResult.EMPTY) {
            return current.hit.subtract(dragStart.hit)
        }
        return null
    }

    protected companion object {
        const val DRAG_PLANE_SIZE = 32.0
    }

}
