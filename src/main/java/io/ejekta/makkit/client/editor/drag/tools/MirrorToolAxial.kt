package io.ejekta.makkit.client.editor.drag.tools

import io.ejekta.makkit.client.editor.EditRegion
import io.ejekta.makkit.client.editor.drag.SingleAxisDragTool
import io.ejekta.makkit.client.render.RenderColor
import io.ejekta.makkit.common.editor.operations.MirrorOperation
import io.ejekta.makkit.common.ext.*
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

internal class MirrorToolAxial(
    ctx: EditRegion.HandleContext
) : SingleAxisDragTool(ctx) {

    // The distance between the original selection and the mirrored section, for display
    private var mirrorDist = 0

    private var projectionVector = Vec3d.ZERO

    private var mirrorPlane = EMPTY_BOX

    // Constrain to direction
    override fun getCursorOffset(snapped: Boolean): Vec3d {
        return super.getCursorOffset(snapped).dirMasked(toolDir)
    }

    override fun onStopDragging() {
        val old = Box(region.selection.calcPos(), region.selection.calcEnd())
        super.onStopDragging()
        region.doOperation(MirrorOperation(mirrorPlane.calcPos()), editBox = old, direction = toolDir)
    }

    override fun getPreviewBox(offset: Vec3d, box: Box): Box {

        mirrorDist = offset.axisValue(toolDir.axis).roundToInt().absoluteValue

        val selectedFace = box.getFacePlane(toolDir)



        mirrorPlane = selectedFace.projectedIn(
                toolDir,
                offset.getComponentAlongAxis(toolDir.axis) * 0.5
        )

        val mirrorPos = selectedFace
                .calcPos()
                .flipAround(mirrorPlane.calcPos())
                .refitForSize(box.getSize(), toolDir)

        return Box(
                mirrorPos,
                mirrorPos.add(box.getSize())
        )

    }

    override fun onDrawPreview(offset: Vec3d) {
        super.onDrawPreview(offset)

        mirrorPlane.draw(fillColor, edgeColor)
        mirrorPlane.drawTextOnFace(
                toolDir,
                mirrorDist.toString()
        )
    }

    companion object {
        val fillColor = RenderColor.PURPLE.toAlpha(.2f)
        val edgeColor = RenderColor.PINK.toAlpha(.3f)
    }

}