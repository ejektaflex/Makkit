package io.ejekta.makkit.client.editor.drag.tools

import io.ejekta.makkit.client.editor.EditRegion
import io.ejekta.makkit.client.editor.drag.SingleAxisDragTool
import io.ejekta.makkit.client.render.RenderColor
import io.ejekta.makkit.client.render.RenderHelper
import io.ejekta.makkit.common.ext.*
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

internal class ResizeToolSymmetric (
    ctx: EditRegion.HandleContext
) : SingleAxisDragTool(ctx) {

    override fun getPreviewBox(offset: Vec3d, box: Box): Box {
        // this locks to an axis and flips so that "positive" is in the direction direction
        val change = offset.dirMasked(toolDir)
        return Box(box.calcPos().subtract(change), box.calcEnd().add(change))
    }

    override fun onDrawPreview(offset: Vec3d) {
        super.onDrawPreview(offset)

        val faceCenter = toolPreviewBox.renderBox.center
        val length = getPreviewSizeIn(toolDir) / 2 - 0.25
        val lineStart = faceCenter.projectedIn(toolDir, length)
        val lineEnd = faceCenter.projectedIn(toolDir, -length)
        RenderHelper.drawLine(lineStart, lineEnd, RenderColor.WHITE)

        toolPreviewBox.renderBox.drawSizeOnFace(toolDir)
    }

}