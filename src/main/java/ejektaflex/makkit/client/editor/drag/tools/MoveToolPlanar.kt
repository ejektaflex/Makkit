package ejektaflex.makkit.client.editor.drag.tools

import ejektaflex.makkit.client.editor.EditRegion
import ejektaflex.makkit.client.editor.drag.DualAxisDragTool
import ejektaflex.makkit.client.editor.input.KeyStateHandler
import ejektaflex.makkit.common.ext.snapped
import net.minecraft.util.math.Box

internal class MoveToolPlanar(region: EditRegion, binding: KeyStateHandler) : DualAxisDragTool(region, binding) {

    override fun calcSelectionBox(snap: Boolean): Box? {
        val offset = getDrawOffset(plane.box)
        if (offset != null) {
            val rounding = offset.snapped(snap)
            return region.area.box.offset(rounding)
        }
        return null
    }

    override fun onDrawPreview() {
        super.onDrawPreview()
        preview.draw()
        val offset = getDrawOffset(plane.box)
        if (offset != null) {
            preview.drawNearAxisLabels(offset)
        }
    }

}