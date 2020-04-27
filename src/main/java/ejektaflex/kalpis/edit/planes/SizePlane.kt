package ejektaflex.kalpis.edit.planes

import ejektaflex.kalpis.edit.EditRegion
import ejektaflex.kalpis.edit.drag.Drag
import ejektaflex.kalpis.ext.dirMask
import ejektaflex.kalpis.ext.round
import ejektaflex.kalpis.ext.vec3d
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d

class SizePlane(region: EditRegion) : Plane(region) {

    override fun shouldDraw(): Boolean {
        return region.stretchDrag.isDragging() || region.shrinkDrag.isDragging()
    }

    override fun getDrawOffset(drag: Drag): Vec3d? {
        return super.getDrawOffset(drag)?.dirMask(drag.start!!.dir)
    }

    fun Box.shrinkSide(off: Vec3d, dir: Direction): Box {
        var a = x1
        var b = y1
        var c = z1


        var d = x2
        var e = y2
        var f = z2

        if (dir.vector.x * a < 0) {
            a -= off.x
        } else {
            d += off.x
        }

        if (dir.vector.y * b < 0) {
            b -= off.y
        } else {
            e += off.y
        }

        if (dir.vector.z * c < 0) {
            c -= off.z
        } else {
            f += off.z
        }

        return Box(a, b, c, d, e, f)
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

                return when (drag) {
                    region.stretchDrag -> {
                        val shrinkVec = rounding.multiply(drag.start!!.dir.opposite.vec3d())
                        val dir = drag.start!!.dir
                        region.region.box.shrinkSide(shrinkVec.dirMask(dir), dir.opposite)
                    }
                    region.shrinkDrag -> {
                        val shrinkVec = rounding.multiply(drag.start!!.dir.opposite.vec3d())
                        val dir = drag.start!!.dir
                        region.region.box.shrinkSide(shrinkVec.dirMask(dir.opposite), dir)
                    }
                    else -> throw Exception("Unsupported Size Plane Drag!")
                }
            }
        }

        return null
    }

}