package ejektaflex.makkit.client.editor.drag

import ejektaflex.makkit.client.MakkitClient
import ejektaflex.makkit.client.data.BoxTraceResult
import ejektaflex.makkit.client.editor.EditRegion
import ejektaflex.makkit.client.editor.input.InputState
import ejektaflex.makkit.client.editor.input.KeyStateHandler
import ejektaflex.makkit.client.render.RenderBox
import ejektaflex.makkit.client.render.RenderColor
import ejektaflex.makkit.common.ext.getEnd
import ejektaflex.makkit.common.ext.getStart
import ejektaflex.makkit.common.network.pakkits.server.ShadowBoxUpdatePacket
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

internal abstract class DragTool(val region: EditRegion, val keyHandler: KeyStateHandler) {

    // We can have other preview boxes and draw them in [onDrawPreview], we just need at least one
    open val preview = RenderBox().apply {
        fillColor = RenderColor.BLUE.toAlpha(.4f)
        edgeColor = RenderColor.ORANGE.toAlpha(.2f)
    }

    var dragStart = BoxTraceResult.EMPTY

    fun isDragging(): Boolean {
        return dragStart != BoxTraceResult.EMPTY
    }

    /**
     * Calculates a box shape for the tool, given a position
     * @param offset The position of the cursor
     * @param box The starting box region
     */
    abstract fun calcSelectionBox(offset: Vec3d, box: Box): Box

    /**
     * Calculates the position of the drag cursor. May also be snapped to a block grid
     * @param snapped Whether or not to snap the cursor to the block grid
     */
    abstract fun getCursorOffset(snapped: Boolean = MakkitClient.config.gridSnapping): Vec3d?

    /**
     * Draws the tool to the screen, with the given offset
     */
    abstract fun onDrawPreview(offset: Vec3d)

    open fun onStartDragging(start: BoxTraceResult) {
        // Do nothing by default
    }

    fun setSelectionBox(): Box? {
        val offset = getCursorOffset(true)
        return if (offset != null) {
            val box = calcSelectionBox(offset, region.area.box)
            region.area.box = box
            box
        } else {
            null
        }
    }

    fun sendSelectionUpdate(box: Box) {
        ShadowBoxUpdatePacket(
                BlockPos(box.getStart()),
                BlockPos(box.getEnd())
        ).sendToServer()
    }

    open fun onStopDragging(stop: BoxTraceResult) {
        val box = setSelectionBox()
        box?.let {
            sendSelectionUpdate(it)
        }
    }

    fun update() {
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

    fun tryDraw() {
        if (isDragging()) {
            val off = getCursorOffset()
            if (off != null) {
                onDrawPreview(off)
            }
        }
    }

    protected companion object {
        const val DRAG_PLANE_SIZE = 64.0
    }

}
