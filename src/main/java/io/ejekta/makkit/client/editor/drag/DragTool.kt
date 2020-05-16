package io.ejekta.makkit.client.editor.drag

import io.ejekta.makkit.client.MakkitClient
import io.ejekta.makkit.client.data.BoxTraceResult
import io.ejekta.makkit.client.editor.EditRegion
import io.ejekta.makkit.client.editor.input.InputState
import io.ejekta.makkit.client.editor.input.KeyStateHandler
import io.ejekta.makkit.client.render.RenderBox
import io.ejekta.makkit.client.render.RenderColor
import io.ejekta.makkit.common.ext.autoTrace
import io.ejekta.makkit.common.ext.trace
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
    abstract fun getPreviewBox(offset: Vec3d, box: Box): Box

    abstract fun getSelectionBox(offset: Vec3d, oldSelection: Box, preview: Box): Box

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

    fun updateState(updateSelection: Boolean = true): Box? {
        return getCursorOffset(true)?.let {
            val preview = getPreviewBox(it, region.selection)
            if (updateSelection) {
                region.selection = getSelectionBox(it, region.selection, preview)
            }
            preview
        }
    }



    open fun onStopDragging(stop: BoxTraceResult) {
        val box = updateState(updateSelection = true)
    }

    fun update() {
        // Try to start dragging
        if (dragStart == BoxTraceResult.EMPTY && keyHandler.isDown) {
            dragStart = region.selection.autoTrace()
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
