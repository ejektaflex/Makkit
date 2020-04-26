package ejektaflex.kalpis.ext

import ejektaflex.kalpis.mixin.BoxMixin
import ejektaflex.kalpis.render.RenderHelper
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
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

data class BoxTraceResult(val dir: Direction, val vec: Vec3d)

fun Box.rayTraceForSide(min: Vec3d, max: Vec3d): BoxTraceResult? {
    val ds = doubleArrayOf(1.0)
    val d = max.x - min.x
    val e = max.y - min.y
    val f = max.z - min.z
    val side = BoxMixin.traceCollisionSide(this, min, ds, null, d, e, f)
    return if (side != null) {
        val g = ds[0]
        BoxTraceResult(side, min.add(g * d, g * e, g * f))
    } else {
        null
    }
}

/*
public Optional<Vec3d> rayTrace(Vec3d min, Vec3d max) {
      double[] ds = new double[]{1.0D};
      double d = max.x - min.x;
      double e = max.y - min.y;
      double f = max.z - min.z;
      Direction direction = traceCollisionSide(this, min, ds, (Direction)null, d, e, f);
      if (direction == null) {
         return Optional.empty();
      } else {
         double g = ds[0];
         return Optional.of(min.add(g * d, g * e, g * f));
      }
   }
 */
