package ejektaflex.makkit.client.editor.drag.tools

import ejektaflex.makkit.client.editor.EditRegion
import ejektaflex.makkit.client.editor.drag.SingleAxisDragTool
import ejektaflex.makkit.client.editor.input.KeyStateHandler
import ejektaflex.makkit.client.render.RenderBox
import ejektaflex.makkit.client.render.RenderColor
import ejektaflex.makkit.common.ext.*
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

internal class MirrorToolOpposite(
        region: EditRegion,
        binding: KeyStateHandler
) : SingleAxisDragTool(region, binding) {

    val mirrorPlane = RenderBox().apply {
        fillColor = RenderColor.PURPLE.toAlpha(.2f)
        edgeColor = RenderColor.PINK.toAlpha(.3f)
    }

    // Constrain to direction
    override fun getDrawOffset(snapped: Boolean): Vec3d? {
        return super.getDrawOffset(snapped)?.axisMask(dragStart.dir)
    }

    override fun calcSelectionBox(offset: Vec3d): Box {

        val planarOffset = region.area.pos.add(
                offset.multiply(2.0)
        )

        val boxProto = Box(
                planarOffset,
                planarOffset.add(region.area.box.getSize())
        )

        return boxProto.offsetBy(offset, dragStart.dir)

    }

    override fun onDrawPreview(offset: Vec3d) {

        mirrorPlane.box = region.area.getFacePlane(dragStart.dir).offsetBy(offset, dragStart.dir)
        mirrorPlane.draw()

        super.onDrawPreview(offset)

    }

}