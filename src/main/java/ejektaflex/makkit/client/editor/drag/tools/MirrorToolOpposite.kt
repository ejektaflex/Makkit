package ejektaflex.makkit.client.editor.drag.tools

import ejektaflex.makkit.client.MakkitClient
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
    override fun getDrawOffset(box: Box): Vec3d? {
        return super.getDrawOffset(box)?.axisMask(dragStart.dir)
    }

    override fun calcSelectionBox(snap: Boolean): Box? {

        val off = nearestPlaneOffset(snap) ?: return null

        val planarOffset = region.area.pos.add(
                off.multiply(2.0)
        )

        val boxProto = Box(
                planarOffset,
                planarOffset.add(region.area.box.getSize())
        )

        return boxProto.offsetBy(off, dragStart.dir)

    }

    override fun onDrawPreview() {

        val off = nearestPlaneOffset(!MakkitClient.config.gridSnapping) ?: return

        mirrorPlane.box = region.area.getFacePlane(dragStart.dir).offsetBy(off, dragStart.dir)

        mirrorPlane.draw()

        super.onDrawPreview()

    }

}