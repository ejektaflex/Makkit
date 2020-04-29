package ejektaflex.kalpis.edit.drag.tools

import ejektaflex.kalpis.edit.EditRegion
import ejektaflex.kalpis.edit.drag.SinglePlaneDragTool
import ejektaflex.kalpis.ext.round
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding
import net.minecraft.util.math.Box

internal class DualAxisMoveTool(region: EditRegion, binding: FabricKeyBinding) : SinglePlaneDragTool(region, binding) {

    override fun calcDragBox(smooth: Boolean): Box? {

        if (isDragging()) {
            val offset = getDrawOffset(plane.box)

            if (offset != null) {

                val rounding = when (smooth) {
                    true -> offset
                    false -> offset.round()
                }

                return region.area.box.offset(rounding)
            }
        }

        return null
    }

}