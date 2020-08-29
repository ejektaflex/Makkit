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

class AnimBox(inTarget: Box = Box(BlockPos.ORIGIN), setup: RenderBox.() -> Unit) {

    val render: RenderBox = RenderBox()

    var isAnimating: Boolean = false
        private set

    var animTime: Long = 0

    var animLength: Long = 4


    val isComplete: Boolean
        get() = animTime == animLength

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
            animTime = 0L
        }
    }

    fun resize(box: Box) {
        startAnimating()
        target = box
    }

    fun snapToTarget() {
        render.box = target
        animTime = animLength
    }

    fun snapTo(box: Box) {
        target = box
        snapToTarget()
    }

    fun update(dt: Long) {

        if (!MakkitClient.config.animations) {
            render.box = target
            return
        }

        // TODO Change to distance checking instead of equality
        if (render.box != target && !isAnimating) {
            start = render.box
            startAnimating()
            //startAnimating()
        }

        //animTime = (animTime + (dt * 1)).coerceIn(0, animLength)

        val func = Easing.InSine.func

        val spd = 25.0
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


        if (isComplete) {
            isAnimating = false
        }

    }

    fun draw() {
        render.draw()
    }

}