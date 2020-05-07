package ejektaflex.makkit.common.ext

import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i
import kotlin.math.max
import kotlin.math.min

fun max(vecA: Vec3d, vecB: Vec3d): Vec3d {
    return Vec3d(
            max(vecA.x, vecB.x),
            max(vecA.y, vecB.y),
            max(vecA.z, vecB.z)
    )
}
/*
fun min(vecA: Vec3d, vecB: Vec3d): Vec3d {
    return Vec3d(
            min(vecA.x, vecB.x),
            min(vecA.y, vecB.y),
            min(vecA.z, vecB.z)
    )
}
 */

val Vec3d.ZERO: Vec3d
    get() = Vec3d(0.0, 0.0, 0.0)

fun Vec3d.ONE(): Vec3d {
    return Vec3d(1.0, 1.0, 1.0)
}

operator fun Vec3d.plus(other: Vec3d): Vec3d {
    return this.add(other)
}

fun Vec3d.round(): Vec3d {
    return Vec3d(kotlin.math.round(x), kotlin.math.round(y), kotlin.math.round(z))
}

// 1 -> 0, 0 -> 1
private fun intSwitch(i: Int): Int {
    return (kotlin.math.abs(i) - 1) * -1
}

fun Vec3d.flipMask(dir: Direction): Vec3d {
    val unit = dir.vector
    val mask = Vec3i(intSwitch(unit.x), intSwitch(unit.y), intSwitch(unit.z))
    return Vec3d(x * mask.x, y * mask.y, z * mask.z)
}

fun Vec3d.dirMask(dir: Direction): Vec3d {
    val unit = dir.vector
    return Vec3d(x * unit.x, y * unit.y, z * unit.z)
}

fun Vec3d.mapFunc(func: (it: Double) -> Double): Vec3d {
    return Vec3d(func(x), func(y), func(z))
}

fun Vec3d.edgeLengthBetweenFaces(a: Direction, b: Direction): Double {
    return arr()[enumValues<Direction.Axis>().first {
        it != a.axis && it != b.axis
    }.ordinal]
}

fun Vec3d.arr(): DoubleArray {
    return doubleArrayOf(x, y, z)
}

fun Vec3d.axisValue(axis: Direction.Axis): Double {
    return axis.choose(x, y, z)
}
