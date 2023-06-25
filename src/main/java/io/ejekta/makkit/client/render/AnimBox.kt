package io.ejekta.makkit.client.render

import io.ejekta.makkit.client.MakkitClient
import io.ejekta.makkit.common.ext.getEnd
import io.ejekta.makkit.common.ext.getStart
import io.ejekta.makkit.common.ext.plus
import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import kotlin.math.*
import kotlin.math.min

class AnimBox(inTarget: Box = Box(BlockPos.ORIGIN), setup: RenderBox.() -> Unit = {}) {

    val render: RenderBox = RenderBox()

    var isAnimating: Boolean = false
        private set

    var start: Box = inTarget
        private set

    var target: Box = inTarget
        private set

    init {
        render.apply(setup)
    }

    fun directSet(box: Box) {
        render.box = box
    }

    fun startAnimating() {
        if (!isAnimating) {
            isAnimating = true
        }
    }

    fun resize(box: Box) {
        startAnimating()
        target = box
    }

    fun snapToTarget() {
        render.box = target
    }

    fun shrinkToCenter() {
        render.box = Box(
                target.center,
                target.center
        )
    }

    fun snapTo(box: Box) {
        target = box
        snapToTarget()
    }

    fun update(dt: Long) {

        if (!MakkitClient.animations) {
            render.box = target
            return
        }

        val spd = MakkitClient.animationSpeed
        val v = spd / 1000.0
        val m = 1.0 / v


        render.box = Box(
                render.box.getStart() + (
                        target.getStart().subtract(render.box.getStart()).multiply(
                                (dt.coerceAtMost((m * 0.5).roundToLong()) * v)
                        )
                ),
                render.box.getEnd() + (
                        target.getEnd().subtract(render.box.getEnd()).multiply(
                                (dt.coerceAtMost((m * 0.5).roundToLong()) * v)
                        )
                )
        )


    }

    fun draw() {
        render.draw()
    }

}