package ejektaflex.makkit.client.editor.drag.tools

import ejektaflex.makkit.client.editor.EditRegion
import ejektaflex.makkit.client.editor.drag.SingleAxisDragTool
import ejektaflex.makkit.client.editor.input.KeyStateHandler
import ejektaflex.makkit.client.render.RenderBox
import ejektaflex.makkit.client.render.RenderColor
import ejektaflex.makkit.common.ext.*
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

internal class MirrorToolOpposite(
        region: EditRegion,
        binding: KeyStateHandler
) : SingleAxisDragTool(region, binding) {

    // The distance between the original selection and the mirrored section, for display
    private var mirrorDist = 0

    private val mirrorPlane = RenderBox().apply {
        fillColor = RenderColor.PURPLE.toAlpha(.2f)
        edgeColor = RenderColor.PINK.toAlpha(.3f)
    }

    // Constrain to direction
    override fun getCursorOffset(snapped: Boolean): Vec3d? {
        return super.getCursorOffset(snapped)?.axisMask(dragStart.dir)
    }

    override fun calcSelectionBox(offset: Vec3d, box: Box): Box {

        mirrorDist = offset.axisValue(dragStart.dir.axis).roundToInt().absoluteValue

        val selectedFace = box.getFacePlane(dragStart.dir)

        mirrorPlane.box = selectedFace
                .offsetBy(offset.multiply(0.5), dragStart.dir)

        val mirrorPos = selectedFace
                .getStart()
                .flipAround(mirrorPlane.box.getStart())
                .refitForSize(box.getSize(), dragStart.dir)

        return Box(
                mirrorPos,
                mirrorPos.add(box.getSize())
        )

    }

    override fun onDrawPreview(offset: Vec3d) {
        super.onDrawPreview(offset)

        mirrorPlane.draw()
        mirrorPlane.drawTextOnFace(
                dragStart.dir,
                mirrorDist.toString()
        )
    }

}