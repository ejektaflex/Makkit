package io.ejekta.makkit.client.editor.drag.tools

import io.ejekta.makkit.client.editor.EditRegion
import io.ejekta.makkit.client.editor.drag.SingleAxisDragTool
import io.ejekta.makkit.client.render.RenderColor
import io.ejekta.makkit.client.render.RenderHelper
import io.ejekta.makkit.common.ext.*
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import kotlin.math.roundToInt

internal class ResizeToolAxial(
    ctx: EditRegion.HandleContext
) : SingleAxisDragTool(ctx) {

    // Constrain to direction
    override fun getCursorOffset(snapped: Boolean): Vec3d {
        return super.getCursorOffset(snapped).dirMasked(toolDir)
    }

    override fun getPreviewBox(offset: Vec3d, box: Box): Box {
        val shrinkVec = offset.dirMasked(toolDir.opposite)
        return box.shrinkSide(shrinkVec, toolDir)
    }

    override fun onDrawPreview(offset: Vec3d) {
        super.onDrawPreview(offset)

        val faceCenter = toolPreviewBox.renderBox.center
        val length = toolPreviewBox.renderBox.sizeInDirection(toolDir) / 2 - 0.25
        val lineStart = faceCenter.projectedIn(toolDir, length)
        val lineEnd = faceCenter.projectedIn(toolDir, -length)
        RenderHelper.drawLine(lineStart, lineEnd, RenderColor.WHITE)

        //preview.render.drawSizeOnFace(dragStart.dir)
        toolPreviewBox.renderBox.drawTextOnFace(
            toolDir, handle.handleBox.sizeOnAxis(toolDir.axis).roundToInt().toString()
        )
    }

}