package ejektaflex.kalpis.ext

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d


fun BlockPos.vec3d(): Vec3d {
    return Vec3d(x.toDouble(), y.toDouble(), z.toDouble())
}


operator fun BlockPos.plus(other: BlockPos): BlockPos {
    return add(other.x, other.y, other.z)
}