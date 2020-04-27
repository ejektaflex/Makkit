package ejektaflex.kalpis.edit.drag

import ejektaflex.kalpis.data.BoxTraceResult
import ejektaflex.kalpis.edit.EditRegion
import ejektaflex.kalpis.edit.IEditor
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding

class Drag(val region: EditRegion, val binding: FabricKeyBinding) : IEditor {

    var start: BoxTraceResult? = null

    fun isDragging(): Boolean {
        return start != null
    }

    override fun update() {
        // Try to start dragging
        if (start == null && binding.isPressed) {
            start = region.blocksRender.trace()
            if (start != null) {
                region.onStartDragging(this, start!!)
            }
        }

        // Try to stop dragging
        if (start != null && !binding.isPressed) {
            region.onStopDragging(this, start!!)
            start = null
        }
    }


}