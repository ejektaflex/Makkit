package io.ejekta.makkit.client.editor.drag.tools

import io.ejekta.makkit.client.editor.EditRegion
import io.ejekta.makkit.client.editor.drag.SingleAxisDragTool
import io.ejekta.makkit.client.render.RenderColor
import io.ejekta.makkit.client.render.RenderHelper
import io.ejekta.makkit.common.ext.*
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import kotlin.math.roundToInt

internal class MoveToolAxial(
    ctx: EditRegion.HandleContext
) : SingleAxisDragTool(ctx) {

    // Constrain to direction
    override fun getCursorOffset(snapped: Boolean): Vec3d {
        return super.getCursorOffset(snapped).axisMasked(dragStart.dir)
    }

    override fun getPreviewBox(offset: Vec3d, box: Box): Box {
        return box.offset(offset)
    }

    override fun onDrawPreview(offset: Vec3d) {
        super.onDrawPreview(offset)

        // TODO is getPreviewBox box always region selection?? Can we remove it?
        //preview.setImmediate(getPreviewBox(offset, region.selection))
        //preview.snap()

        //val pbox = getPreviewBox(offset, region.selection)

        val faceCenter = toolPreviewBox.renderBox.center
        val length = getPreviewSizeIn(dragStart.dir) / 2 - 0.25
        val lineStart = faceCenter.projectedIn(dragStart.dir, length)
        val lineEnd = faceCenter.projectedIn(dragStart.dir, -length)
        RenderHelper.drawLine(lineStart, lineEnd, RenderColor.WHITE)

        toolPreviewBox.renderBox.drawTextOnFace(
            dragStart.dir,
            toolPreviewBox.renderBox.calcPos().subtract(
                    region.selection.calcPos()
            ).axisValue(dragStart.dir.axis).roundToInt().toString()
        )
    }

}