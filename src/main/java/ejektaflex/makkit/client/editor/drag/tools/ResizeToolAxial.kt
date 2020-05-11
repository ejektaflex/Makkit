package ejektaflex.makkit.client.editor.drag.tools

import ejektaflex.makkit.client.editor.EditRegion
import ejektaflex.makkit.client.editor.drag.SingleAxisDragTool
import ejektaflex.makkit.client.editor.input.KeyStateHandler
import ejektaflex.makkit.common.ext.*
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

internal class ResizeToolAxial(
        region: EditRegion,
        binding: KeyStateHandler
) : SingleAxisDragTool(region, binding) {

    // Constrain to direction
    override fun getDrawOffset(box: Box): Vec3d? {
        return super.getDrawOffset(box)?.dirMask(dragStart.dir)
    }

    override fun calcSelectionBox(snap: Boolean): Box? {

        val offset = nearestPlaneOffset(snap) ?: return null
        val shrinkVec = offset.dirMask(dragStart.dir.opposite)

        return region.area.box.shrinkSide(shrinkVec, dragStart.dir)
    }

}