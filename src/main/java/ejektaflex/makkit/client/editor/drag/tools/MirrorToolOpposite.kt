package ejektaflex.makkit.client.editor.drag.tools

import ejektaflex.makkit.client.editor.EditRegion
import ejektaflex.makkit.client.editor.drag.SingleAxisDragTool
import ejektaflex.makkit.client.editor.input.KeyStateHandler
import ejektaflex.makkit.client.render.RenderBox
import ejektaflex.makkit.client.render.RenderColor
import ejektaflex.makkit.client.render.RenderHelper
import ejektaflex.makkit.common.ext.*
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import kotlin.math.abs
import kotlin.math.roundToInt

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
        return super.getCursorOffset(snapped)?.axisMask(dragStart.dir)
    }

    override fun calcSelectionBox(offset: Vec3d, box: Box): Box {

        mirrorPlane.box = box.getFacePlane(dragStart.dir).offsetBy(offset.multiply(0.5), dragStart.dir)

        val mirrorPos = box.getStart().add(
                offset.multiply(1.0).add(box.getSize().dirMask(dragStart.dir))
        )

        mirrorPos

        // If this condition is true, we shouldn't be returning a box.
        if (offset.dirMask(dragStart.dir).axisValue(dragStart.dir.axis) < 0) {
            RenderHelper.drawPoint(dragStart.hit, size = 0.25)
        }

        return Box(
                mirrorPos,
                mirrorPos.add(box.getSize())
        )

    }

    override fun onDrawPreview(offset: Vec3d) {
        super.onDrawPreview(offset)

        val absDist = abs(
                preview.box.getStart()
                        .subtract(region.area.box.getStart())
                        .plus(region.area.box.getSize())
                        .axisValue(dragStart.dir.axis)
                        .roundToInt()
        )

        mirrorPlane.draw()
        mirrorPlane.drawTextOnFace(
                dragStart.dir,
                absDist.toString()
        )

    }

}