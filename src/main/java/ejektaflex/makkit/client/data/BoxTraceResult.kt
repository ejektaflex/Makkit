package ejektaflex.makkit.client.data

import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d

data class BoxTraceResult(val source: Vec3d, val dir: Direction, val hit: Vec3d) {
    companion object {
        val EMPTY = BoxTraceResult(
                Vec3d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE),
                Direction.UP,
                Vec3d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE)
        )
    }
}
