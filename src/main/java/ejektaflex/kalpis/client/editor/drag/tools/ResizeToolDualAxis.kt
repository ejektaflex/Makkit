package ejektaflex.kalpis.client.editor.drag.tools

import ejektaflex.kalpis.client.editor.EditRegion
import ejektaflex.kalpis.client.editor.drag.DualAxisDragTools
import ejektaflex.kalpis.client.editor.input.KeyStateHandler
import ejektaflex.kalpis.common.ext.otherDirectionalAxes
import ejektaflex.kalpis.common.ext.round
import ejektaflex.kalpis.common.ext.shrinkSide
import net.minecraft.util.math.Box

internal class ResizeToolDualAxis(region: EditRegion, binding: KeyStateHandler) : DualAxisDragTools(region, binding) {

    override fun onDraw() {
        super.onDraw()
        region.preview.draw()
        val offset = getDrawOffset(plane.box)
        if (offset != null) {
            region.preview.drawNearAxisLabels(offset)
        }
    }

    override fun calcDragBox(smooth: Boolean): Box? {
        if (isDragging()) {
            val offset = getDrawOffset(plane.box)

            if (offset != null) {
                val rounding = when (smooth) {
                    true -> offset
                    false -> offset.round()
                }

                val shrinkVec = rounding.multiply(-0.5)
                val shrinkDirs = start!!.dir.otherDirectionalAxes()

                var proto = region.area.box
                for (otherDir in shrinkDirs) {
                    proto = proto.shrinkSide(shrinkVec, otherDir)
                }

                return proto
            }
        }
        return null
    }

}