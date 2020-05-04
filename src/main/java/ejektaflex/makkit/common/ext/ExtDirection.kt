package ejektaflex.makkit.common.ext

import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d


fun Direction.otherDirectionalAxes(): List<Direction> {
    return enumValues<Direction>().filter {
        it.direction == this.direction && it != this
    }
}

fun Direction.vec3d(): Vec3d {
    return Vec3d(unitVector)
}

