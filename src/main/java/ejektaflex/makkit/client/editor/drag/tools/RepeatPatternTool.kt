package ejektaflex.makkit.client.editor.drag.tools

import ejektaflex.makkit.client.data.BoxTraceResult
import ejektaflex.makkit.client.editor.EditRegion
import ejektaflex.makkit.client.editor.drag.SingleAxisDragTool
import ejektaflex.makkit.client.editor.input.KeyStateHandler
import ejektaflex.makkit.common.ext.*
import ejektaflex.makkit.common.network.pakkits.server.EditWorldPacket
import ejektaflex.makkit.common.world.RepeatOperation
import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import kotlin.math.roundToInt

internal class RepeatPatternTool(
        region: EditRegion,
        binding: KeyStateHandler
) : SingleAxisDragTool(region, binding) {

    var beforeBox: Box? = null

    override fun onDraw() {
        super.onDraw()
        region.preview.draw()

        region.preview.drawTextOn(
                dragStart!!.dir,
                region.preview.box.sizeInDirection(dragStart!!.dir).roundToInt().toString()
        )
    }

    override fun onStartDragging(start: BoxTraceResult) {
        super.onStartDragging(start)

        beforeBox = region.area.box
    }

    override fun calcDragBox(smooth: Boolean): Box? {
        if (!isDragging()) {
            return null
        }

        val offsets = planes.mapNotNull {
            getDrawOffset(it.box)
        }

        if (offsets.isEmpty()) {
            return null
        }

        // Only use the offset of the closer of the two planes
        val offsetToUse = offsets.minBy { it.distanceTo(dragStart!!.source) }!!
        val change = offsetToUse.multiply(dragStart!!.dir.vec3d().abs())

        val roundedChange = when (smooth) {
            true -> change
            false -> change.round()
        }

        return region.area.box.stretch(roundedChange)
    }

    override fun onStopDragging(stop: BoxTraceResult) {
        super.onStopDragging(stop)

        if (beforeBox == null) {
            return
        }
        val startBox = beforeBox!!

        EditWorldPacket(
            BlockPos(region.area.pos),
            BlockPos(region.area.end),
            Direction.NORTH, // doesn't matter
            RepeatOperation(startBox),
            listOf(MinecraftClient.getInstance().player!!.mainHandStack)
        ).sendToServer()
    }

}
