package ejektaflex.makkit.client.editor.drag.tools.clipboard


import ejektaflex.makkit.client.data.BoxTraceResult
import ejektaflex.makkit.client.editor.EditRegion
import ejektaflex.makkit.client.editor.drag.DualAxisDragTool
import ejektaflex.makkit.client.editor.input.KeyStateHandler
import ejektaflex.makkit.common.enum.ClipboardMode
import ejektaflex.makkit.common.ext.getStart
import ejektaflex.makkit.common.network.pakkits.server.ClipboardIntentPacket
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

internal abstract class ClipboardTool(region: EditRegion, binding: KeyStateHandler) : DualAxisDragTool(region, binding) {

    abstract val mode: ClipboardMode

    override fun calcSelectionBox(offset: Vec3d, box: Box) = box

    override fun onStartDragging(start: BoxTraceResult) {
        println("Sending ${mode} at ${region.area.box}")
        super.onStartDragging(start)
        ClipboardIntentPacket(
                mode,
                dragStart.dir,
                region.area.box
        ).sendToServer()
    }

    override fun onDrawPreview(offset: Vec3d) {
        super.onDrawPreview(offset)
        preview.draw()
        preview.drawNearAxisLabels(region.area.box.getStart())
    }

}