package io.ejekta.makkit.client.editor.drag.tools

import io.ejekta.makkit.client.editor.EditRegion
import io.ejekta.makkit.client.editor.drag.SingleAxisDragTool
import io.ejekta.makkit.client.editor.handle.Handle
import io.ejekta.makkit.client.render.RenderColor
import io.ejekta.makkit.client.render.RenderHelper
import io.ejekta.makkit.common.ext.*
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import kotlin.math.roundToInt

internal class ResizeToolAxial(
    handle: Handle
) : SingleAxisDragTool(handle) {

    // Constrain to direction
    override fun getCursorOffset(snapped: Boolean): Vec3d? {
        return super.getCursorOffset(snapped)?.dirMasked(dragStart.dir)
    }

    override fun getPreviewBox(offset: Vec3d, box: Box): Box {
        val shrinkVec = offset.dirMasked(dragStart.dir.opposite)
        return box.shrinkSide(shrinkVec, dragStart.dir)
    }

    override fun onDrawPreview(offset: Vec3d) {
        super.onDrawPreview(offset)

        val faceCenter = preview.renderBox.center
        val length = preview.renderBox.sizeInDirection(dragStart.dir) / 2 - 0.25
        val lineStart = faceCenter.projectedIn(dragStart.dir, length)
        val lineEnd = faceCenter.projectedIn(dragStart.dir, -length)
        RenderHelper.drawLine(lineStart, lineEnd, RenderColor.WHITE)

        //preview.render.drawSizeOnFace(dragStart.dir)
        preview.renderBox.drawTextOnFace(
                dragStart.dir, handle.handleBox.sizeOnAxis(dragStart.dir.axis).roundToInt().toString()
        )
    }

}