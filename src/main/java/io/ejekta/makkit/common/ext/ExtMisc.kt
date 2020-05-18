package io.ejekta.makkit.common.ext

import io.ejekta.makkit.client.render.RenderColor
import io.ejekta.makkit.client.render.RenderHelper
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.Matrix4f
import net.minecraft.util.math.Vec3d
import net.minecraft.util.registry.Registry
import kotlin.random.Random
import kotlin.random.nextInt

fun MatrixStack.drawOffset(pos: Vec3d, func: RenderHelper.() -> Unit, helper: RenderHelper) {
    translate(-pos.x, -pos.y, -pos.z)
    func(helper)
    translate(pos.x, pos.y, pos.z)
}

val ItemStack.identifier: Identifier
    get() = Registry.ITEM.getId(item)

// Shorthand
val ItemStack.id: Identifier
    get() = identifier

fun <T : Any> List<T>.weightedRandomBy(func: T.() -> Int): T {
    val mapped = map { it to func(it) }.toMap()
    return mapped.weightedRandom()
}

// 1, 1
// sum = 2
// when point = 1
// gold, 1
// RETURN

fun <T : Any> Map<T, Int>.weightedRandom(): T {
    val sum = values.sum()

    if (sum == 0) {
        throw Exception("Weighted Random List should not have a sum of 0!")
    }

    var point = (1..sum).random()

    for ((item, weight) in this) {
        if (point <= weight) {
            return item
        }
        point -= weight
    }
    return keys.last()
}


// Inlining here may improve performance simply because this gets called very often

inline fun VertexConsumer.vertex(mat: Matrix4f, x: Double, y: Double, z: Double): VertexConsumer {
    return vertex(mat, x.toFloat(), y.toFloat(), z.toFloat())
}

inline fun VertexConsumer.color(col: RenderColor): VertexConsumer {
    return color(col.r, col.g, col.b, col.a)
}













