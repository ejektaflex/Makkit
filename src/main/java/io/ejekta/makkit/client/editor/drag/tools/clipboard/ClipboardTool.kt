package io.ejekta.makkit.client.editor.drag.tools.clipboard


import io.ejekta.makkit.client.data.BoxTraceResult
import io.ejekta.makkit.client.editor.EditRegion
import io.ejekta.makkit.client.editor.drag.DualAxisDragTool
import io.ejekta.makkit.common.enums.ClipboardMode
import io.ejekta.makkit.common.ext.getStart
import io.ejekta.makkit.common.network.pakkits.server.ClipboardIntentPacket
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

internal abstract class ClipboardTool(region: EditRegion) : DualAxisDragTool(region) {

    abstract val mode: ClipboardMode

    override fun getPreviewBox(offset: Vec3d, box: Box) = box

    override fun onStartDragging(start: BoxTraceResult) {
        super.onStartDragging(start)
        ClipboardIntentPacket(
                mode,
                dragStart.dir,
                region.selection
        ).sendToServer()
    }

    override fun onDrawPreview(offset: Vec3d) {
        super.onDrawPreview(offset)
        preview.render.drawNearAxisLabels(region.selection.getStart())
    }

}