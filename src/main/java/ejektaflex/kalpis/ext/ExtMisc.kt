package ejektaflex.kalpis.ext

import ejektaflex.kalpis.data.BoxTraceResult
import ejektaflex.kalpis.mixin.BoxMixin
import ejektaflex.kalpis.render.RenderColor
import ejektaflex.kalpis.render.RenderHelper
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.*

fun MatrixStack.drawOffset(pos: Vec3d, func: RenderHelper.() -> Unit, helper: RenderHelper) {
    translate(-pos.x, -pos.y, -pos.z)
    func(helper)
    translate(pos.x, pos.y, pos.z)
}

inline fun VertexConsumer.vertex(mat: Matrix4f, x: Double, y: Double, z: Double): VertexConsumer {
    return vertex(mat, x.toFloat(), y.toFloat(), z.toFloat())
}

inline fun VertexConsumer.color(col: RenderColor): VertexConsumer {
    return color(col.r, col.g, col.b, col.a)
}












