package io.ejekta.makkit.common.ext

import io.ejekta.makkit.client.MakkitClient
import io.ejekta.makkit.client.data.BoxTraceResult
import io.ejekta.makkit.client.mixin.BoxMixin
import io.ejekta.makkit.client.render.RenderHelper
import io.ejekta.makkit.common.enums.SideSelectionStyle
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d

private fun Box.trace(
        reverse: Boolean = false // TODO re implement back selecting toggle
): BoxTraceResult {
    return RenderHelper.boxTrace(this, reverse = reverse)
}

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

fun Box.forEachBlockCoord(func: (x: Int, y: Int, z: Int) -> Unit) {
    val start = getStart()
    val size = getSize()
    for (x in start.x.toInt() until (start.x + size.x).toInt()) {
        for (y in start.y.toInt() until (start.y + size.y).toInt()) {
            for (z in start.z.toInt() until (start.z + size.z).toInt()) {
                func(x, y, z)
            }
        }
    }
}

fun Box.projectedIn(dir: Direction, amt: Double): Box {
    return offset(Vec3d(amt, amt, amt).axisMask(dir))
    //return add(Vec3d(amt, amt, amt).dirMask(dir))
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
            boxStart.add(faceSize)
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
    return Vec3d(minX, minY, minZ)
}

fun Box.getSize(): Vec3d {
    return Vec3d(maxX - minX, maxY - minY, maxZ - minZ)
}

fun Box.blockSize(): BlockPos {
    return BlockPos(getSize())
}

fun Box.getEnd(): Vec3d {
    return Vec3d(maxX, maxY, maxZ)
}

fun Box.startBlock(): BlockPos {
    return BlockPos(getStart())
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
    val startPos = BlockPos(minX, minY, minZ)
    val endPos = BlockPos(maxX, maxY, maxZ)
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
    val startPos = BlockPos(minX, minY, minZ)
    val endPos = BlockPos(maxX, maxY, maxZ)
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

fun Box.faceDimensions(side: Direction): List<Double> {
    val sideSizeVec = getFacePlane(side).getSize()
    val otherAxes = enumValues<Direction.Axis>().filter { it != side.axis }
    return otherAxes.map { sideSizeVec.axisValue(it) }
}

fun Box.faceSqArea(side: Direction): Double {
    val dims = faceDimensions(side)
    return dims[0] * dims[1]
}

fun Box.longestAxisLength(): Double {
    val size = getSize()
    return enumValues<Direction.Axis>().maxBy {
        size.getComponentAlongAxis(it)
    }!!.let { size.getComponentAlongAxis(it) }
}

fun Box.autoTrace(): BoxTraceResult {
    return when (MakkitClient.config.sideSelectionStyle) {
        SideSelectionStyle.SIMPLE -> simpleTrace()
        SideSelectionStyle.SMART -> smartTrace()
        SideSelectionStyle.EXPERIMENTAL -> geniusTrace()
    }
}

private fun Box.simpleTrace(): BoxTraceResult {
    return trace()
}

private fun Box.smartTrace(): BoxTraceResult {
    val front = trace(reverse = false)
    val back = trace(reverse = true)
    val nonEmpty = listOf(front, back).filter { it != BoxTraceResult.EMPTY }

    return when {
        nonEmpty.isEmpty() -> {
            BoxTraceResult.EMPTY
        }
        nonEmpty.size == 1 -> {
            nonEmpty.first()
        }
        else -> {
            nonEmpty.minBy { faceCenterPos(it.dir).distanceTo(it.hit) }!!
        }
    }
}

private fun Box.geniusTrace(): BoxTraceResult {
    val front = trace(reverse = false)
    val back = trace(reverse = true)
    val nonEmpty = listOf(front, back).filter { it != BoxTraceResult.EMPTY }

    return when {
        nonEmpty.isEmpty() -> {
            BoxTraceResult.EMPTY
        }
        nonEmpty.size == 1 -> {
            nonEmpty.first()
        }
        else -> {
            nonEmpty
                    .mapIndexed { i, btr -> i to btr }
                    .minBy { faceCenterPos(it.second.dir).distanceTo(it.second.hit) * (it.first + 2) }!!.second
        }
    }
}
