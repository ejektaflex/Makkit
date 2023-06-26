package io.ejekta.makkit.client.render

import io.ejekta.makkit.client.data.BoxTraceResult
import io.ejekta.makkit.common.ext.*
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import kotlin.math.roundToInt

class RenderBox(inPos: Vec3d = Vec3d(0.0, 0.0, 0.0), inPos2: Vec3d = Vec3d(1.0, 1.0, 1.0)) {

    constructor(inBox: Box) : this(inBox.getStart(), inBox.getEnd())

    var box = Box(inPos, inPos2)

    val pos: Vec3d
        get() = Vec3d(box.minX, box.minY, box.minZ)

    val end: Vec3d
        get() = Vec3d(box.maxX, box.maxY, box.maxZ)

    val size: Vec3d
        get() = end.subtract(pos)

    fun drawNearAxisLabels(func: () -> Vec3d) {
        val dirs = RenderHelper.getLookDirections().toList()

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

    fun drawBackFacePlanes() {
        val dirs = RenderHelper.getLookBehindDirections()


        dirs.forEachIndexed { i, direction ->

            var fp = box.getFacePlane(direction)

            val flatStretchies = Vec3d(3.0, 3.0, 3.0).reverseMask(direction)
            fp = fp.expand(flatStretchies.x, flatStretchies.y, flatStretchies.z)

            RenderBox(
                fp
            ).draw(RenderColor.BLUE.toAlpha(0.2f))

        }
    }

    fun drawAxisPositions() {
        drawNearAxisLabels(pos)
    }

    fun drawTextOnFace(face: Direction, text: String) {
        RenderHelper.drawText(
                box.faceCenterPos(face),
                text
        )
    }

    fun drawSizeOnFace(face: Direction) {
        drawTextOnFace(face, box.sizeOnAxis(face.axis).roundToInt().toString())
    }

    fun getFacePlane(dir: Direction): Box {
        return box.getFacePlane(dir)
    }

    fun drawFace(dir: Direction, colorIn: RenderColor) {
        RenderBox(getFacePlane(dir)).draw(colorIn, colorIn)
    }

    var fillColor: RenderColor = RenderColor.WHITE.toAlpha(.2f)
    var edgeColor: RenderColor = RenderColor.WHITE.toAlpha(.2f)

    fun draw(colorFill: RenderColor? = null, colorEdge: RenderColor? = null, offset: Vec3d = Vec3d.ZERO) {
        RenderHelper.drawBoxFilled(box.offset(offset), colorFill ?: fillColor)
        RenderHelper.drawBoxEdges(box.offset(offset), colorEdge ?: edgeColor)
    }

    fun trace(reverse: Boolean = false): BoxTraceResult {
        return RenderHelper.boxTrace(box, reverse = reverse)
    }

}