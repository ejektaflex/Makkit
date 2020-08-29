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

    var animTime: Long = 0

    var animLength: Long = 1000

    val animPercent: Double
        get() = (animTime.toDouble() / animLength)

    var target: Box = inTarget
        private set

    init {
        render.apply(setup)
    }

    fun directSet(box: Box) {
        render.box = box
    }

    fun resize(box: Box) {
        //startTime = System.currentTimeMillis()
        animTime = 0L
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

        if (render.box != target) {
            //println("booyah")
            animTime = 0L
        }

        //println(dt)

        if (!MinecraftClient.getInstance().isPaused) {
            //println("TIME WAS $animTime")
        }

        animTime = (animTime + (dt * 10)).coerceIn(0, animLength)

        val func = Easing.InSine.func

        if (!MinecraftClient.getInstance().isPaused) {
            //println("$dt\t\t$animTime\t\t$animPercent\t\t${func(animPercent)}")
        }

        render.box = Box(
                lerp(render.box.getStart(), target.getStart(), func(animPercent)),
                lerp(render.box.getEnd(),  target.getEnd(), func(animPercent))
        )

    }

    fun draw() {
        render.draw()
    }

}