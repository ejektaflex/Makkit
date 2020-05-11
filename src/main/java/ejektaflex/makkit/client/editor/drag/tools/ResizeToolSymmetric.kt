package ejektaflex.makkit.client.editor.drag.tools

import ejektaflex.makkit.client.editor.EditRegion
import ejektaflex.makkit.client.editor.drag.SingleAxisDragTool
import ejektaflex.makkit.client.editor.input.KeyStateHandler
import ejektaflex.makkit.common.ext.*
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

internal class ResizeToolSymmetric (
        region: EditRegion,
        binding: KeyStateHandler
) : SingleAxisDragTool(region, binding) {

    override fun calcSelectionBox(offset: Vec3d): Box {

        // this locks to an axis and flips so that "positive" is in the direction direction
        val change = offset.dirMask(dragStart.dir)

        val box = region.area.box
        return Box(box.getStart().subtract(change), box.getEnd().add(change))
    }

}