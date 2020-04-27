package ejektaflex.kalpis.render

import ejektaflex.kalpis.data.BoxTraceResult
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

class RenderBox(inPos: Vec3d = Vec3d(0.0, 0.0, 0.0), inSize: Vec3d = Vec3d(1.0, 1.0, 1.0)) {

    var box = Box(inPos, inPos.add(inSize))

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