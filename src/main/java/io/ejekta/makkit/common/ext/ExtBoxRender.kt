package io.ejekta.makkit.common.ext

import io.ejekta.makkit.client.render.RenderColor
import io.ejekta.makkit.client.render.RenderHelper
import io.ejekta.makkit.common.ext.*
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import kotlin.math.roundToInt

val EMPTY_BOX: Box = Box(Vec3d.ZERO, Vec3d.ZERO)

fun Box.drawNearAxisLabels(func: () -> Vec3d) {
    val dirs = RenderHelper.getLookDirections().toList()

    dirs.forEachIndexed { i, direction ->
        val shifted = dirs[(i + 1) % 3]
        val dirForLen = dirs[(i + 2) % 3]
        val pos = edgeCenterPos(direction, shifted)
        RenderHelper.drawText(pos, func().axisValue(dirForLen.axis).roundToInt().toString())
    }
}

fun Box.drawNearAxisLabels(vec3d: Vec3d) = drawNearAxisLabels { vec3d }

fun Box.drawAxisSizes() {
    drawNearAxisLabels(getSize())
}

fun Box.drawAxisPositions() {
    drawNearAxisLabels(calcPos())
}

fun Box.drawTextOnFace(face: Direction, text: String) {
    RenderHelper.drawText(
        faceCenterPos(face),
        text
    )
}

fun Box.drawSizeOnFace(face: Direction) {
    drawTextOnFace(face, sizeOnAxis(face.axis).roundToInt().toString())
}

fun Box.drawFace(dir: Direction, colorIn: RenderColor) {
    getFacePlane(dir).draw(colorIn, colorIn)
}

fun Box.draw(colorFill: RenderColor, colorEdge: RenderColor = colorFill, offset: Vec3d = Vec3d.ZERO) {
    RenderHelper.drawBoxFilled(offset(offset), colorFill)
    RenderHelper.drawBoxEdges(offset(offset), colorEdge)
}










