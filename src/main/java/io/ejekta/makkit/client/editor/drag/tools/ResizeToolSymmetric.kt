package io.ejekta.makkit.client.editor.drag.tools

import io.ejekta.makkit.client.MakkitClient
import io.ejekta.makkit.client.config.MakkitConfig
import io.ejekta.makkit.client.editor.EditRegion
import io.ejekta.makkit.client.editor.drag.SingleAxisDragTool
import io.ejekta.makkit.client.editor.input.KeyStateHandler
import io.ejekta.makkit.common.ext.dirMask
import io.ejekta.makkit.common.ext.getEnd
import io.ejekta.makkit.common.ext.getStart
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

internal class ResizeToolSymmetric (
        region: EditRegion
) : SingleAxisDragTool(region) {

    override val keyHandler: KeyStateHandler
        get() = MakkitClient.config.resizeSymmetricBinding

    override fun getPreviewBox(offset: Vec3d, box: Box): Box {
        // this locks to an axis and flips so that "positive" is in the direction direction
        val change = offset.dirMask(dragStart.dir)
        return Box(box.getStart().subtract(change), box.getEnd().add(change))
    }

    override fun onDrawPreview(offset: Vec3d) {
        super.onDrawPreview(offset)
        preview.drawSizeOnFace(dragStart.dir)
    }

}