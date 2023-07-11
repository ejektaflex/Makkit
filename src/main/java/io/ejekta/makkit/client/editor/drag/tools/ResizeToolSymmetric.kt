package io.ejekta.makkit.client.editor.drag.tools

import io.ejekta.makkit.client.editor.EditRegion
import io.ejekta.makkit.client.editor.drag.SingleAxisDragTool
import io.ejekta.makkit.client.render.RenderColor
import io.ejekta.makkit.client.render.RenderHelper
import io.ejekta.makkit.common.ext.dirMasked
import io.ejekta.makkit.common.ext.getEnd
import io.ejekta.makkit.common.ext.getStart
import io.ejekta.makkit.common.ext.projectedIn
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

internal class ResizeToolSymmetric (
        region: EditRegion
) : SingleAxisDragTool(region) {

    override fun getPreviewBox(offset: Vec3d, box: Box): Box {
        // this locks to an axis and flips so that "positive" is in the direction direction
        val change = offset.dirMasked(dragStart.dir)
        return Box(box.getStart().subtract(change), box.getEnd().add(change))
    }

    override fun onDrawPreview(offset: Vec3d) {
        super.onDrawPreview(offset)

        val faceCenter = preview.render.box.center
        val length = getPreviewSizeIn(dragStart.dir) / 2 - 0.25
        val lineStart = faceCenter.projectedIn(dragStart.dir, length)
        val lineEnd = faceCenter.projectedIn(dragStart.dir, -length)
        RenderHelper.drawLine(lineStart, lineEnd, RenderColor.WHITE)

        preview.render.drawSizeOnFace(dragStart.dir)
    }

}