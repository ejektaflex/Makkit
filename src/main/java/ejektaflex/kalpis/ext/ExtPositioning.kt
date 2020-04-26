package ejektaflex.kalpis.ext

import ejektaflex.kalpis.render.RenderHelper
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

fun MatrixStack.drawOffset(pos: Vec3d, func: RenderHelper.() -> Unit, helper: RenderHelper) {
    translate(-pos.x, -pos.y, -pos.z)
    func(helper)
    translate(pos.x, pos.y, pos.z)
}

private fun BlockPos.vec3d(): Vec3d {
    return Vec3d(x.toDouble(), y.toDouble(), z.toDouble())
}

operator fun Vec3d.plus(other: Vec3d): Vec3d {
    return this.add(other)
}

operator fun BlockPos.plus(other: BlockPos): BlockPos {
    return add(other.x, other.y, other.z)
}