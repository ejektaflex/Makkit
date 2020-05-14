package io.ejekta.makkit.common.ext

import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i
import kotlin.math.max
import kotlin.math.abs
import kotlin.math.round

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

fun List<Vec3d>.average(): Vec3d {
    return if (isEmpty()) {
        throw Exception("Cannot average an empty list of Vec3d! Must contain at least one element!")
    } else {
        val summed = this.reduce { a, b -> a.add(b) }
        summed.multiply(1.0 / size)
    }
}

operator fun Vec3d.plus(other: Vec3d): Vec3d {
    return this.add(other)
}

operator fun Vec3d.minus(other: Vec3d): Vec3d {
    return this.subtract(other)
}


operator fun Vec3d.times(other: Vec3d): Vec3d {
    return this.multiply(other)
}

operator fun Vec3i.times(other: Vec3i): Vec3i {
    return Vec3i(x * other.x, y * other.y, z * other.z)
}

operator fun Vec3d.times(num: Double): Vec3d {
    return multiply(num)
}

operator fun Vec3i.times(num: Int): Vec3i {
    return Vec3i(x * num, y * num, z * num)
}

fun Vec3d.round(): Vec3d {
    return Vec3d(round(x), round(y), round(z))
}


fun Vec3d.snapped(snap: Boolean): Vec3d {
    return when (snap) {
        true -> this.round()
        false -> this
    }
}

fun Vec3d.roundToVec3i(): Vec3i {
    return Vec3i(x, y, z)
}

fun Vec3d.hasZeroAxis(): Boolean {
    return enumValues<Direction.Axis>().any {
        axisValue(it) == 0.0
    }
}

// 1 -> 0, 0 -> 1
private fun intSwitch(i: Int): Int {
    return (abs(i) - 1) * -1
}

fun Vec3d.flipMask(dir: Direction): Vec3d {
    val unit = dir.vector
    val mask = Vec3i(intSwitch(unit.x), intSwitch(unit.y), intSwitch(unit.z))
    return Vec3d(x * mask.x, y * mask.y, z * mask.z)
}

// Masks a vector with a direction's unit vector
fun Vec3d.dirMask(dir: Direction): Vec3d {
    return multiply(dir.vec3d())
}

// Same as dirMask, but uses an absolute positive unit vector
fun Vec3d.axisMask(dir: Direction): Vec3d {
    return multiply(dir.vec3d().abs())
}

fun Vec3d.mapFunc(func: (it: Double) -> Double): Vec3d {
    return Vec3d(func(x), func(y), func(z))
}

fun Vec3d.edgeLengthBetweenFaces(a: Direction, b: Direction): Double {
    return arr()[enumValues<Direction.Axis>().first {
        it != a.axis && it != b.axis
    }.ordinal]
}

// This is probably in bad taste
fun Vec3d.arr(): DoubleArray {
    return doubleArrayOf(x, y, z)
}

fun Vec3d.axisValue(axis: Direction.Axis): Double {
    return axis.choose(x, y, z)
}

fun Vec3d.abs(): Vec3d {
    return Vec3d(abs(x), abs(y), abs(z))
}

// Simple rotation methods

fun Vec3d.rotateClockwise(center: Vec3d): Vec3d {
    return Vec3d(center.x + center.z - z, y, x + center.z - center.x)
}

fun Vec3i.rotateClockwise(center: Vec3i): Vec3i {
    return Vec3i(center.x + center.z - z, y, x + center.z - center.x)
}

fun Vec3i.rotateClockwise(times: Int): Vec3i {
    var inVec = Vec3i(x, y, z)
    for (i in 0 until times) {
        inVec = inVec.rotateClockwise(Vec3i.ZERO)
    }
    return inVec
}

fun Vec3d.flipAround(center: Vec3d): Vec3d {
    return this - ((this - center) * 2.0)
}

/**
 * Will offset a vector in a certain direction if it's on the negative axis.
 * This is used for Box placement, because the start of a Box must always be
 * a smaller vector than the end of said Box.
 */
fun Vec3d.refitForSize(size: Vec3d, dir: Direction): Vec3d {
    return when (dir.direction) {
        Direction.AxisDirection.POSITIVE -> this
        Direction.AxisDirection.NEGATIVE -> this.subtract(size.axisMask(dir))
    }
}
