package ejektaflex.kalpis.edit.drag.tools

import ejektaflex.kalpis.edit.EditRegion
import ejektaflex.kalpis.edit.drag.SingleAxisDragTool
import ejektaflex.kalpis.ext.*
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import kotlin.math.roundToInt

internal class ResizeToolSingleAxis(
        region: EditRegion,
        binding: FabricKeyBinding,
        val opposite: Boolean = false
) : SingleAxisDragTool(region, binding) {

    // Constrain to direction
    override fun getDrawOffset(box: Box): Vec3d? {
        return super.getDrawOffset(box)?.dirMask(start!!.dir)
    }

    override fun onDraw() {
        super.onDraw()

        region.preview.draw()

        region.preview.drawTextOn(
                start!!.dir,
                region.preview.box.sizeInDirection(start!!.dir).roundToInt().toString()
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

                val shrinkVec = rounding.multiply(start!!.dir.opposite.vec3d())
                val dir = start!!.dir

                return when (opposite) {
                    false -> {
                        region.area.box.shrinkSide(shrinkVec, dir)
                    }
                    true -> {
                        region.area.box.shrinkSide(shrinkVec, dir.opposite)
                    }
                }
            }
        }

        return null
    }

}