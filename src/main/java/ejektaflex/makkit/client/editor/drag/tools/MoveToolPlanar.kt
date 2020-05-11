package ejektaflex.makkit.client.editor.drag.tools

import ejektaflex.makkit.client.editor.EditRegion
import ejektaflex.makkit.client.editor.drag.DualAxisDragTool
import ejektaflex.makkit.client.editor.input.KeyStateHandler
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

internal class MoveToolPlanar(region: EditRegion, binding: KeyStateHandler) : DualAxisDragTool(region, binding) {

    override fun calcSelectionBox(offset: Vec3d, box: Box): Box {
        return box.offset(offset)
    }

    override fun onDrawPreview(offset: Vec3d) {
        super.onDrawPreview(offset)
        preview.draw()
        preview.drawNearAxisLabels(offset)
    }

}