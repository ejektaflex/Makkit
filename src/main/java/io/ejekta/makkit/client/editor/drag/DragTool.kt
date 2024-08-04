package io.ejekta.makkit.client.editor.drag

import io.ejekta.makkit.client.MakkitClient
import io.ejekta.makkit.client.editor.EditRegion
import io.ejekta.makkit.client.editor.Handle
import io.ejekta.makkit.client.render.AnimBox
import io.ejekta.makkit.client.render.RenderColor
import io.ejekta.makkit.common.ext.draw
import io.ejekta.makkit.common.ext.sizeInDirection
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d

abstract class DragTool(val ctx: EditRegion.HandleContext) {

    val region: EditRegion
        get() = ctx.handle.region

    val handle: Handle
        get() = ctx.handle

    val toolDir: Direction
        get() = handle.faceDir

    val previewTarget: Box
        get() = getPreviewBox(getCursorOffset(true))

    // We can have other preview boxes and draw them in [onDrawPreview], we just need at least one
    open val toolPreviewBox = AnimBox({ previewTarget }) {
        draw(fillColor, edgeColor)
    }

    val startVec: Vec3d
        get() = ctx.hit

    protected fun getMainAxis(): Direction.Axis = toolDir.axis

    protected fun getAlternateAxesDirections(): List<Direction> {
        return Direction.entries.filter { it.axis != toolDir.axis }
    }

    protected fun getAlternateAxes(): List<Direction.Axis> {
        return Direction.Axis.entries.filter { it != toolDir.axis }
    }

    protected fun getSelectionSizeIn(direction: Direction): Double {
        return region.selection.sizeInDirection(direction)
    }

    protected fun getPreviewSizeIn(direction: Direction): Double {
        return handle.handleBox.sizeInDirection(direction)
    }

    /**
     * Calculates a box shape for the tool, given a position
     * @param offset The position of the cursor
     * @param box The starting box region
     */
    abstract fun getPreviewBox(offset: Vec3d, box: Box = region.selection): Box

    abstract fun getSelectionBox(offset: Vec3d, oldSelection: Box, preview: Box): Box

    /**
     * Calculates the position of the drag cursor. May also be snapped to a block grid
     * @param snapped Whether to snap the cursor to the block grid
     */
    abstract fun getCursorOffset(snapped: Boolean = MakkitClient.gridSnapping): Vec3d


    open fun onStartDragging() {
        println("Base drag tool drag starting")
        toolPreviewBox.snap()
    }

    fun updateState(updateSelection: Boolean = true): Box {
        return getCursorOffset(true).let {
            val preview = getPreviewBox(it, region.selection)
            if (updateSelection) {
                region.selection = getSelectionBox(it, region.selection, preview)
            }
            preview
        }
    }

    open fun onStopDragging() {
        updateState(updateSelection = true)
    }

    fun update(delta: Long) {
        toolPreviewBox.update(delta)
    }

    fun tryDraw() {
        val off = getCursorOffset()
        onDrawPreview(off)
    }

    /**
     * Draws the tool to the screen, with the given offset
     */
    open fun onDrawPreview(offset: Vec3d) {
        //val newPreview = getPreviewBox(offset, region.selection)
//        if (newPreview != preview.actualBox) {
//            //preview.resize(getPreviewBox(offset, region.selection))
//        }
        toolPreviewBox.draw()
    }

    protected companion object {
        const val DRAG_PLANE_SIZE = 64.0
        val fillColor = RenderColor.BLUE.toAlpha(.4f)
        val edgeColor = RenderColor.ORANGE.toAlpha(.2f)
    }

}
