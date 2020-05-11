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
    override fun getCursorOffset(snapped: Boolean): Vec3d? {
        return super.getCursorOffset(snapped)?.dirMask(dragStart.dir)
    }

    override fun calcSelectionBox(offset: Vec3d, box: Box): Box {

        mirrorPlane.box = box.getFacePlane(dragStart.dir).offsetBy(offset, dragStart.dir)

        val mirrorOff = box.getStart().add(offset)

        val boxProto = Box(
                offset.add(mirrorOff),
                offset.add(mirrorOff).add(box.getSize())
        )

        return boxProto

    }

    override fun onDrawPreview(offset: Vec3d) {

        mirrorPlane.draw()

        super.onDrawPreview(offset)

    }

}