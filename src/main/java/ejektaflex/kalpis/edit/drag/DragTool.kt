package ejektaflex.kalpis.edit.drag

import ejektaflex.kalpis.data.BoxTraceResult
import ejektaflex.kalpis.edit.EditRegion
import ejektaflex.kalpis.edit.IEditor
import ejektaflex.kalpis.render.RenderHelper
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

internal abstract class DragTool(val region: EditRegion, val binding: FabricKeyBinding) : IEditor {

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
        if (start == null && binding.isPressed) {
            start = region.area.trace()
            if (start != null) {
                onStartDragging(start!!)
            }
        }

        // Try to stop dragging
        if (start != null && !binding.isPressed) {
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