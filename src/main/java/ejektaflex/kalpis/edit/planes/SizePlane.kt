package ejektaflex.kalpis.edit.planes

import ejektaflex.kalpis.edit.EditRegion
import ejektaflex.kalpis.edit.drag.Drag
import ejektaflex.kalpis.ext.*
import ejektaflex.kalpis.render.RenderBox
import ejektaflex.kalpis.render.RenderColor
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import java.lang.Math.abs

class SizePlane(region: EditRegion) : Plane(region) {

    override fun shouldDraw(): Boolean {
        return region.stretchDrag.isDragging() || region.shrinkDrag.isDragging()
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

                val shrinkVec = rounding.multiply(drag.start!!.dir.opposite.vec3d())
                val dir = drag.start!!.dir

                /* face plane rendering
                val sideSize = region.region.size.flipMask(dir)

                val width = region.region.size.dirMask(dir)

                val cutPrev = RenderBox(
                        region.region.box.center.subtract(
                                sideSize.multiply(0.5)
                        ).add(
                                width.multiply(0.5)
                        ),
                        sideSize
                )
                
                cutPrev.draw(RenderColor.PINK, offset = Vec3d(0.05, 0.05, 0.05))
                 */



                return when (drag) {
                    region.stretchDrag -> {
                        region.region.box.shrinkSide(shrinkVec, dir.opposite)
                    }
                    region.shrinkDrag -> {
                        region.region.box.shrinkSide(shrinkVec, dir)
                    }
                    else -> throw Exception("Unsupported Size Plane Drag!")
                }
            }
        }

        return null
    }

}