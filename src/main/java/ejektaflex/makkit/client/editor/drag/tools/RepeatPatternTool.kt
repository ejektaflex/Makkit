package ejektaflex.makkit.client.editor.drag.tools

import ejektaflex.makkit.client.data.BoxTraceResult
import ejektaflex.makkit.client.editor.EditRegion
import ejektaflex.makkit.client.editor.drag.SingleAxisDragTool
import ejektaflex.makkit.client.editor.input.KeyStateHandler
import ejektaflex.makkit.common.ext.*
import ejektaflex.makkit.common.network.pakkits.server.EditWorldPacket
import ejektaflex.makkit.common.editor.operations.RepeatOperation
import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction

internal class RepeatPatternTool(
        region: EditRegion,
        binding: KeyStateHandler
) : SingleAxisDragTool(region, binding) {

    private var beforeBox: Box? = null

    override fun onStartDragging(start: BoxTraceResult) {
        super.onStartDragging(start)

        beforeBox = region.area.box
    }

    override fun calcDragBox(smooth: Boolean): Box? {
        if (!isDragging()) {
            return null
        }

        val offset = nearestPlaneOffset(smooth) ?: return null

        return region.area.box.stretch(offset.axisMask(dragStart.dir))
    }

    override fun onStopDragging(stop: BoxTraceResult) {
        super.onStopDragging(stop)
        beforeBox?.let {
            region.doOperation(RepeatOperation(it))
        }
    }

}
