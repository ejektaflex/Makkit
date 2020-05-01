package ejektaflex.kalpis.edit.drag.tools

import ejektaflex.kalpis.edit.EditRegion
import ejektaflex.kalpis.edit.drag.DualAxisDragTools
import ejektaflex.kalpis.ext.otherDirectionalAxes
import ejektaflex.kalpis.ext.round
import ejektaflex.kalpis.ext.shrinkSide
import ejektaflex.kalpis.ext.vec3d
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding
import net.minecraft.util.math.Box

internal class ResizeToolDualAxis(region: EditRegion, binding: FabricKeyBinding) : DualAxisDragTools(region, binding) {

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