package io.ejekta.makkit.client.render

import io.ejekta.makkit.client.MakkitClient
import io.ejekta.makkit.common.ext.EMPTY_BOX
import io.ejekta.makkit.common.ext.calcEnd
import io.ejekta.makkit.common.ext.calcPos
import io.ejekta.makkit.common.ext.plus
import net.minecraft.util.math.Box
import kotlin.math.*

class AnimBox(val realBox: () -> Box, var onDraw: Box.() -> Unit) {

    // The box that is rendered
    var renderBox = realBox()
        private set

    var isAnimating: Boolean = false
        private set

    // The real location of the box
    private val actualBox: Box
        get() = realBox()

    fun setImmediate(box: Box) {
        renderBox = box
    }

    private fun startAnimating() {
        if (!isAnimating) {
            isAnimating = true
        }
    }

    fun shrinkToCenter() {
        val center = actualBox.center
        renderBox = Box(center, center)
    }

    fun snapTo(box: Box) {
        renderBox = actualBox
    }

    fun update(dt: Long) {
        if (!MakkitClient.animations) {
            renderBox = actualBox
            return
        }

        val spd = MakkitClient.animationSpeed
        val v = spd / 1000.0
        val m = 1.0 / v

        renderBox = Box(
                renderBox.calcPos() + (
                        actualBox.calcPos().subtract(renderBox.calcPos()).multiply(
                                (dt.coerceAtMost((m * 0.5).roundToLong()) * v)
                        )
                ),
                renderBox.calcEnd() + (
                        actualBox.calcEnd().subtract(renderBox.calcEnd()).multiply(
                                (dt.coerceAtMost((m * 0.5).roundToLong()) * v)
                        )
                )
        )
    }

    fun draw() {
        renderBox.onDraw()
    }

}