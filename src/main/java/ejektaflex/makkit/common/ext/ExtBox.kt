package ejektaflex.makkit.common.ext

import ejektaflex.makkit.client.data.BoxTraceResult
import ejektaflex.makkit.client.mixin.BoxMixin
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import kotlin.math.max
import kotlin.math.min


fun Box.shrinkSide(off: Vec3d, dir: Direction): Box {

    var vecA = getStart()
    var vecB = getEnd()

    when (dir.direction) {
        Direction.AxisDirection.NEGATIVE -> {
            vecA = vecA.subtract(off)
        }
        Direction.AxisDirection.POSITIVE -> {
            vecB = vecB.subtract(off)
        }
        else -> { throw Exception("This should never happen!") }
    }

    return Box(vecA, vecB)
}

fun Box.offsetBy(vec3d: Vec3d, dir: Direction): Box {
    return offset(vec3d.axisMask(dir))
}

fun Box.getFacePlane(dir: Direction): Box {
    val faceSize = getSize().flipMask(dir)

    val width = getSize().dirMask(dir)

    val boxStart = center.subtract(
            faceSize.multiply(0.5)
    ).add(
            width.multiply(0.5)
    )

    return Box(
            boxStart,
            boxStart + faceSize
    )
}

fun Box.offsetBy(amt: Double, dir: Direction): Box {
    return offset(Vec3d(amt, amt, amt).axisMask(dir))
}

fun Box.resizeBy(amt: Double, dir: Direction): Box {
    return shrinkSide(
            Vec3d(amt, amt, amt).axisMask(dir),
            dir
    )
}


fun Box.withMinSize(minSize: Vec3d): Box {
    val size = getSize()
    return Box(
            getStart(),
            getStart().add(
                    max(
                            getSize(),
                            Vec3d(1.0, 1.0, 1.0)
                    )
            )
    )
}

fun Box.getStart(): Vec3d {
    return Vec3d(x1, y1, z1)
}

fun Box.getSize(): Vec3d {
    return Vec3d(x2 - x1, y2 - y1, z2 - z1)
}

fun Box.getEnd(): Vec3d {
    return Vec3d(x2, y2, z2)
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

// Returns the center point of the edge between the faces of the given directions
// NOTE: Opposite directions will return a zero vector
fun Box.edgeCenterPos(dirA: Direction, dirB: Direction): Vec3d {
    return center.add(
            (dirA.vec3d().add(dirB.vec3d()))
                    .multiply(getSize())
                    .multiply(0.5)
    )
}

fun Box.faceCenterPos(dir: Direction): Vec3d {
    return center.add(
            dir.vec3d()
                    .multiply(getSize())
                    .multiply(0.5)
    )
}

fun Box.sizeOnAxis(axis: Direction.Axis): Double {
    return getSize().axisValue(axis)
}

fun Box.sizeInDirection(dir: Direction): Double {
    return getSize().axisValue(dir.axis)
}

fun Box.positionInDirection(dir: Direction): Double {
    return getStart().axisValue(dir.axis)
}

fun Box.positionOffsetInDirection(dir: Direction, other: Box): Double {
    return getStart().axisValue(dir.axis) - other.getStart().axisValue(dir.axis)
}

fun Box.getBlockArray(): List<BlockPos> {
    val buff = mutableListOf<BlockPos>()
    val startPos = BlockPos(x1, y1, z1)
    val endPos = BlockPos(x2, y2, z2)
    for (dx in startPos.x until endPos.x) {
        for (dy in startPos.y until endPos.y) {
            for (dz in startPos.z until endPos.z) {
                buff.add(BlockPos(dx, dy, dz))
            }
        }
    }
    return buff
}

fun Box.wallBlocks(): List<BlockPos> {
    val buff = mutableListOf<BlockPos>()
    val startPos = BlockPos(x1, y1, z1)
    val endPos = BlockPos(x2, y2, z2)
    for (dx in startPos.x until endPos.x) {
        for (dy in startPos.y until endPos.y) {
            for (dz in startPos.z until endPos.z) {
                if (dx == startPos.x || dx == endPos.x - 1 || dz == startPos.z || dz == endPos.z - 1) {
                    buff.add(BlockPos(dx, dy, dz))
                }
            }
        }
    }
    return buff
}
