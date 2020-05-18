package io.ejekta.makkit.client.editor.drag.tools

import io.ejekta.makkit.client.MakkitClient
import io.ejekta.makkit.client.config.MakkitConfig
import io.ejekta.makkit.client.editor.EditRegion
import io.ejekta.makkit.client.editor.drag.SingleAxisDragTool
import io.ejekta.makkit.client.editor.input.KeyStateHandler
import io.ejekta.makkit.common.ext.dirMask
import io.ejekta.makkit.common.ext.shrinkSide
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

internal class ResizeToolAxial(
        region: EditRegion
) : SingleAxisDragTool(region) {

    override val keyHandler: KeyStateHandler
        get() = MakkitClient.config.resizeSideKey

    // Constrain to direction
    override fun getCursorOffset(snapped: Boolean): Vec3d? {
        return super.getCursorOffset(snapped)?.dirMask(dragStart.dir)
    }

    override fun getPreviewBox(offset: Vec3d, box: Box): Box {
        val shrinkVec = offset.dirMask(dragStart.dir.opposite)
        return box.shrinkSide(shrinkVec, dragStart.dir)
    }

    override fun onDrawPreview(offset: Vec3d) {
        super.onDrawPreview(offset)
        preview.drawSizeOnFace(dragStart.dir)
    }

}