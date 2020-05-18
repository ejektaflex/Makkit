package io.ejekta.makkit.client.editor.drag.tools

import io.ejekta.makkit.client.MakkitClient
import io.ejekta.makkit.client.editor.EditRegion
import io.ejekta.makkit.client.editor.drag.DualAxisDragTool
import io.ejekta.makkit.client.editor.input.KeyStateHandler
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
        preview.drawNearAxisLabels(offset)
    }

}