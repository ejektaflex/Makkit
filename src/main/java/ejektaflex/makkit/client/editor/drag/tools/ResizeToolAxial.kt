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
    override fun getDrawOffset(snapped: Boolean): Vec3d? {
        return super.getDrawOffset(snapped)?.dirMask(dragStart.dir)
    }

    override fun calcSelectionBox(offset: Vec3d): Box {
        val shrinkVec = offset.dirMask(dragStart.dir.opposite)

        return region.area.box.shrinkSide(shrinkVec, dragStart.dir)
    }

}