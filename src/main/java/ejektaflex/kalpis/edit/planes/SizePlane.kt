package ejektaflex.kalpis.edit.planes

import ejektaflex.kalpis.edit.EditRegion
import ejektaflex.kalpis.edit.drag.Drag
import ejektaflex.kalpis.ext.dirMask
import ejektaflex.kalpis.ext.round
import ejektaflex.kalpis.ext.vec3d
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

class SizePlane(region: EditRegion) : Plane(region) {

    override fun shouldDraw(): Boolean {
        return region.sizeDrag.isDragging()
    }

    override fun getDrawOffset(drag: Drag): Vec3d? {
        return super.getDrawOffset(drag)?.dirMask(drag.start!!.dir)
    }

    override fun calcDragBox(drag: Drag, smooth: Boolean, otherPlanes: List<Plane>): Box? {

        val allPlanes = mutableListOf<Plane>(this).apply {
            addAll(otherPlanes)
        }

        if (drag.isDragging()) {

            val offsets = allPlanes.mapNotNull {
                it.getDrawOffset(drag)
            }

            if (offsets.isNotEmpty()) {
                val offsetToUse = offsets.minBy { it.distanceTo(drag.start!!.start) }!!

                val rounding = when (smooth) {
                    true -> offsetToUse
                    false -> offsetToUse.round()
                }

                return region.region.box.stretch(
                        rounding.multiply(drag.start!!.dir.vec3d())
                )
            }
        }

        return null
    }

}