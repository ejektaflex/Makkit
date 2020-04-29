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













