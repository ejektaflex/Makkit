package ejektaflex.kalpis.client.editor.drag

import ejektaflex.kalpis.client.data.BoxTraceResult
import ejektaflex.kalpis.client.editor.EditRegion
import ejektaflex.kalpis.client.editor.IEditor
import ejektaflex.kalpis.client.editor.input.InputState
import ejektaflex.kalpis.client.editor.input.KeyStateHandler
import ejektaflex.kalpis.render.RenderHelper
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

internal abstract class DragTool(val region: EditRegion, val keyHandler: KeyStateHandler) : IEditor {

    var start: BoxTraceResult? = null

    fun isDragging(): Boolean {
        return start != null
    }

    override fun shouldDraw(): Boolean {
        return isDragging()
    }

    abstract fun onStartDragging(start: BoxTraceResult)

    abstract fun onStopDragging(stop: BoxTraceResult)

    abstract fun calcDragBox(smooth: Boolean): Box?

    override fun update() {
        // Try to start dragging
        if (start == null && keyHandler.isDown) {
            start = region.area.trace(reverse = InputState.isBackSelecting)
            if (start != null) {
                onStartDragging(start!!)
            }
        }

        // Try to stop dragging
        if (start != null && !keyHandler.isDown) {
            onStopDragging(start!!)
            start = null
        }

    }

    open fun getDrawOffset(box: Box): Vec3d? {
        val current = RenderHelper.boxTraceForSide(box)
        if (start != null && current != null) {
            return current.hit.subtract(start!!.hit)
        }
        return null
    }



}