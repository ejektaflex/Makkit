package ejektaflex.kalpis.client.data

import ejektaflex.kalpis.common.ext.flipMask
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d

data class BoxTraceResult(val start: Vec3d, val dir: Direction, val hit: Vec3d) {

    val sideVec: Vec3d
        get() {
            val f = dir.unitVector
            return Vec3d(f.x.toDouble(), f.y.toDouble(), f.z.toDouble())
        }

    fun hitMask(): Vec3d {
        return hit.flipMask(dir)
    }


}
