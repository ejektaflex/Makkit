package io.ejekta.makkit.client.render

import io.ejekta.makkit.client.MakkitClient
import io.ejekta.makkit.common.ext.getEnd
import io.ejekta.makkit.common.ext.getStart
import io.ejekta.makkit.common.ext.plus
import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import kotlin.math.PI
import kotlin.math.cos

class AnimBox(inTarget: Box = Box(BlockPos.ORIGIN), setup: RenderBox.() -> Unit) {

    val render: RenderBox = RenderBox()

    var isAnimating: Boolean = false
        private set

    var animTime: Long = 0

    var animLength: Long = 1000

    val animPercent: Double
        get() = (animTime.toDouble() / animLength)

    val isComplete: Boolean
        get() = animTime == animLength

    var target: Box = inTarget
        private set

    init {
        render.apply(setup)
    }

    fun directSet(box: Box) {
        render.box = box
    }

    fun startAnimating() {
        isAnimating = true
        animTime = 0L
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

        if (render.box != target && !isAnimating) {
            //println("booyah")
            startAnimating()
        }

        if (isAnimating) {
            animTime = (animTime + (dt * 1)).coerceIn(0, animLength)

            val func = Easing.InSine.func

            render.box = Box(
                    lerp(render.box.getStart(), target.getStart(), func(animPercent)),
                    lerp(render.box.getEnd(),  target.getEnd(), func(animPercent))
            )

            if (isComplete) {
                isAnimating = false
            }
        }

    }

    fun draw() {
        render.draw()
    }

}