package io.ejekta.makkit.client.render

import net.minecraft.util.math.Vec3d

fun lerp(v1: Double, v2: Double, t: Double): Double {
    return (1.0 - t) * v1 + t * v2
}

fun lerp(v1: Vec3d, v2: Vec3d, amt: Double): Vec3d {
    return Vec3d(
            lerp(v1.x, v2.x, amt),
            lerp(v1.y, v2.y, amt),
            lerp(v1.z, v2.z, amt)
    )
}