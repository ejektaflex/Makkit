package ejektaflex.makkit.client.render

import ejektaflex.makkit.client.data.BoxTraceResult
import ejektaflex.makkit.common.ext.*
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import kotlin.math.roundToInt

class RenderBox(inPos: Vec3d = Vec3d(0.0, 0.0, 0.0), inPos2: Vec3d = Vec3d(1.0, 1.0, 1.0)) {

    constructor(inBox: Box) : this(inBox.getStart(), inBox.getEnd())

    var box = Box(inPos, inPos2)

    val pos: Vec3d
        get() = Vec3d(box.x1, box.y1, box.z1)

    val end: Vec3d
        get() = Vec3d(box.x2, box.y2, box.z2)

    val size: Vec3d
        get() = end.subtract(pos)

    fun drawNearAxisLabels(func: () -> Vec3d) {
        val dirs = RenderHelper.getLookDirections()

        dirs.forEachIndexed { i, direction ->
            val shifted = dirs[(i + 1) % 3]
            val dirForLen = dirs[(i + 2) % 3]
            val pos = box.edgeCenterPos(direction, shifted)
            RenderHelper.drawText(pos, func().axisValue(dirForLen.axis).roundToInt().toString())
        }
    }

    fun drawNearAxisLabels(vec3d: Vec3d) {
        drawNearAxisLabels { vec3d }
    }

    fun drawAxisSizes() {
        drawNearAxisLabels(size)
    }

    fun drawAxisPositions() {
        drawNearAxisLabels(pos)
    }

    fun drawTextOn(face: Direction, text: String) {
        RenderHelper.drawText(
                box.faceCenterPos(face),
                text
        )
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
                boxStart + faceSize
        )
    }

    fun drawFace(dir: Direction, colorIn: RenderColor) {
        RenderBox(getFacePlane(dir)).draw(colorIn, colorIn)
    }

    var fillColor: RenderColor = RenderColor.WHITE
    var edgeColor: RenderColor = RenderColor.WHITE

    fun draw(colorFill: RenderColor? = null, colorEdge: RenderColor? = null, offset: Vec3d = Vec3d.ZERO) {
        RenderHelper.drawBoxFilled(box.offset(offset), colorFill ?: fillColor)
        RenderHelper.drawBoxEdges(box.offset(offset), colorEdge ?: edgeColor)
    }

    fun trace(reverse: Boolean = false): BoxTraceResult {
        return RenderHelper.boxTrace(box, reverse = reverse)
    }

}