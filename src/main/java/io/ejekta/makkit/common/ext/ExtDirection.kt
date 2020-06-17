package io.ejekta.makkit.common.ext

import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d


fun Direction.otherDirectionsSameSigNum(): List<Direction> {
    return enumValues<Direction>().filter {
        it.direction == this.direction && it != this
    }
}

fun Direction.alternateAxesDirs(): List<Direction> {
    return enumValues<Direction>().filter {
        it.axis != this.axis
    }
}

fun Direction.Axis.others(): List<Direction.Axis> {
    return enumValues<Direction.Axis>().filter { it != this }
}

fun Direction.rotatedClockwise(times: Int): Direction {
    return Direction.fromHorizontal((this.horizontal + times))
}

fun Direction.vec3d(): Vec3d {
    return Vec3d(vector.x.toDouble(), vector.y.toDouble(), vector.z.toDouble())
}

