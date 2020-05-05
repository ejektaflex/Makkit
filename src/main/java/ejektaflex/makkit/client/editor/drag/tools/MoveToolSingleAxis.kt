package ejektaflex.makkit.client.editor.drag.tools

import ejektaflex.makkit.client.editor.EditRegion
import ejektaflex.makkit.client.editor.drag.SingleAxisDragTool
import ejektaflex.makkit.client.editor.input.KeyStateHandler
import ejektaflex.makkit.client.render.RenderColor
import ejektaflex.makkit.common.ext.*
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import kotlin.math.roundToInt

internal class MoveToolSingleAxis(
        region: EditRegion,
        binding: KeyStateHandler,
        val opposite: Boolean = false
) : SingleAxisDragTool(region, binding) {

    // Constrain to direction
    override fun getDrawOffset(box: Box): Vec3d? {
        return super.getDrawOffset(box)?.dirMask(start!!.dir)
    }

    override fun onDraw() {
        super.onDraw()
        region.preview.draw(edgeColor = RenderColor.ORANGE)

        region.preview.drawTextOn(
                start!!.dir,
                region.preview.box.positionOffsetInDirection(start!!.dir, region.area.box).roundToInt().toString()
        )
    }

    override fun calcDragBox(smooth: Boolean): Box? {
        if (isDragging()) {
            val offsets = planes.mapNotNull {
                getDrawOffset(it.box)
            }

            if (offsets.isNotEmpty()) {
                val offsetToUse = offsets.minBy { it.distanceTo(start!!.start) }!!

                val rounding = when (smooth) {
                    true -> offsetToUse
                    false -> offsetToUse.round()
                }

                return region.area.box.offset(rounding)
            }
        }

        return null
    }

}