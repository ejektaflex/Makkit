package ejektaflex.kalpis.edit.drag

import ejektaflex.kalpis.ExampleMod
import ejektaflex.kalpis.data.BoxTraceResult
import ejektaflex.kalpis.edit.EditRegion
import ejektaflex.kalpis.edit.IEditor

class Drag(val region: EditRegion) : IEditor {

    var start: BoxTraceResult? = null

    fun isDragging(): Boolean {
        return start != null
    }


    override fun update() {
        // Try to start dragging
        if (start == null && ExampleMod.dragBinding.isPressed) {
            start = region.blocksRender.trace()
            if (start != null) {
                region.onStartDragging(start!!)
            }
        }

        // Try to stop dragging
        if (start != null && !ExampleMod.dragBinding.isPressed) {
            region.onStopDragging(start!!)
            start = null
        }
    }


}