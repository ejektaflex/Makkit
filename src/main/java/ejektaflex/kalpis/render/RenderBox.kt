package ejektaflex.kalpis.render

import ejektaflex.kalpis.data.BoxTraceResult
import ejektaflex.kalpis.ext.*
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import java.util.*

class RenderBox(inPos: Vec3d = Vec3d(0.0, 0.0, 0.0), inPos2: Vec3d = Vec3d(1.0, 1.0, 1.0)) {

    constructor(inBox: Box) : this(inBox.getStart(), inBox.getEnd())

    var box = Box(inPos, inPos2)

    fun getBlockArray(): List<BlockPos> {
        val buff = mutableListOf<BlockPos>()
        val startPos = BlockPos(pos)
        val endPos = BlockPos(end)
        for (dx in startPos.x until endPos.x) {
            for (dy in startPos.y until endPos.y) {
                for (dz in startPos.z until endPos.z) {
                    buff.add(BlockPos(dx, dy, dz))
                }
            }
        }
        return buff
    }

    fun drawAxisNumbers() {
        val dirs = RenderHelper.getLookDirections()

        dirs.forEachIndexed { i, direction ->
            val shifted = dirs[(i + 1) % 3]
            val dirForLen = dirs[(i + 2) % 3]

            val pos = box.edgeCenterPos(direction, shifted)

            RenderHelper.drawText(pos, size.axisValue(dirForLen.axis).toInt().toString())
        }
    }

    fun getFacePlane(dir: Direction): Box {
        val faceSize = size.flipMask(dir)

        val width = size.dirMask(dir)

        val boxStart = box.center.subtract(
                faceSize.multiply(0.5)
        ).add(
                width.multiply(0.5)
        )

        return Box(
                boxStart,
                //sideSize
                boxStart + faceSize
        )
    }

    fun drawFace(dir: Direction, colorIn: RenderColor? = RenderColor.PINK) {
        RenderBox(getFacePlane(dir)).draw(colorIn)
    }

    val pos: Vec3d
        get() = Vec3d(box.x1, box.y1, box.z1)

    val end: Vec3d
        get() = Vec3d(box.x2, box.y2, box.z2)

    val size: Vec3d
        get() = end.subtract(pos)


    var color: RenderColor = RenderColor.WHITE

    fun draw(colorIn: RenderColor? = null, offset: Vec3d = Vec3d.ZERO) {
        RenderHelper.drawBox(box.offset(offset), colorIn ?: color)
    }

    fun trace(): BoxTraceResult? {
        return RenderHelper.boxTraceForSide(box)
    }

}