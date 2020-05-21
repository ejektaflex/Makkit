package io.ejekta.makkit.client.editor.drag.tools

import io.ejekta.makkit.client.MakkitClient
import io.ejekta.makkit.client.editor.EditRegion
import io.ejekta.makkit.client.editor.drag.DualAxisDragTool
import io.ejekta.makkit.client.editor.input.KeyStateHandler
import io.ejekta.makkit.client.render.RenderColor
import io.ejekta.makkit.client.render.RenderHelper
import io.ejekta.makkit.common.ext.getFacePlane
import io.ejekta.makkit.common.ext.projectedIn
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

internal class MoveToolPlanar(region: EditRegion) : DualAxisDragTool(region) {

    override val keyHandler: KeyStateHandler
        get() = MakkitClient.config.moveDragKey

    override fun getPreviewBox(offset: Vec3d, box: Box): Box {
        return box.offset(offset)
    }

    override fun onDrawPreview(offset: Vec3d) {
        super.onDrawPreview(offset)
        preview.draw()

        val faceCenter = preview.box.getFacePlane(dragStart.dir).center
        for (axisDir in getAlternateAxesDirections()) {
            val length = getSelectionSizeIn(axisDir) / 2 - 0.25
            val lineStart = faceCenter.projectedIn(axisDir, length)
            val lineEnd = faceCenter.projectedIn(axisDir, -length)
            RenderHelper.drawLine(lineStart, lineEnd, RenderColor.WHITE)
        }

        preview.drawNearAxisLabels(offset)
    }

}