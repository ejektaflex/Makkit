package ejektaflex.kalpis.edit.drag.tools

import ejektaflex.kalpis.edit.EditRegion
import ejektaflex.kalpis.edit.drag.DualAxisDragTools
import ejektaflex.kalpis.ext.round
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding
import net.minecraft.util.math.Box

internal class MoveToolsDualAxis(region: EditRegion, binding: FabricKeyBinding) : DualAxisDragTools(region, binding) {

    override fun onDraw() {
        super.onDraw()

        region.preview.draw()
        // TODO Change to drawing position data
        region.preview.drawAxisNumbers()
    }

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