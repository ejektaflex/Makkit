package ejektaflex.kalpis.ext

import ejektaflex.kalpis.data.BoxTraceResult
import ejektaflex.kalpis.mixin.BoxMixin
import ejektaflex.kalpis.render.RenderHelper
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.*

fun MatrixStack.drawOffset(pos: Vec3d, func: RenderHelper.() -> Unit, helper: RenderHelper) {
    translate(-pos.x, -pos.y, -pos.z)
    func(helper)
    translate(pos.x, pos.y, pos.z)
}

fun BlockPos.vec3d(): Vec3d {
    return Vec3d(x.toDouble(), y.toDouble(), z.toDouble())
}

operator fun Vec3d.plus(other: Vec3d): Vec3d {
    return this.add(other)
}

operator fun BlockPos.plus(other: BlockPos): BlockPos {
    return add(other.x, other.y, other.z)
}

fun Vec3d.round(): Vec3d {
    return Vec3d(kotlin.math.round(x), kotlin.math.round(y), kotlin.math.round(z))
}

fun Box.rayTraceForSide(min: Vec3d, max: Vec3d): BoxTraceResult? {
    val ds = doubleArrayOf(1.0)
    val d = max.x - min.x
    val e = max.y - min.y
    val f = max.z - min.z
    val side = BoxMixin.traceCollisionSide(this, min, ds, null, d, e, f)
    return if (side != null) {
        val g = ds[0]
        BoxTraceResult(min, side, min.add(g * d, g * e, g * f))
    } else {
        null
    }
}

fun Direction.otherDirectionalAxes(): List<Direction> {
    return enumValues<Direction>().filter {
        it.direction == this.direction && it != this
    }
}

fun Direction.vec3d(): Vec3d {
    return Vec3d(unitVector)
}

val Vec3d.ZERO: Vec3d
    get() = Vec3d(0.0, 0.0, 0.0)

val Vec3d.ONE: Vec3d
    get() = Vec3d(1.0, 1.0, 1.0)

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

fun Box.getStart(): Vec3d {
    return Vec3d(x1, y1, z1)
}

fun Box.getEnd(): Vec3d {
    return Vec3d(x2, y2, z2)
}
