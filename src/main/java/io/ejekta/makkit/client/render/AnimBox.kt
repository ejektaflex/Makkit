package io.ejekta.makkit.client.render

import io.ejekta.makkit.client.MakkitClient
import io.ejekta.makkit.common.ext.getEnd
import io.ejekta.makkit.common.ext.getStart
import io.ejekta.makkit.common.ext.plus
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box

class AnimBox(inTarget: Box = Box(BlockPos.ORIGIN), setup: RenderBox.() -> Unit) {

    val render: RenderBox = RenderBox()

    var target: Box = inTarget
        private set

    init {
        render.apply(setup)
    }

    fun directSet(box: Box) {
        render.box = box
    }

    fun resize(box: Box) {
        target = box
    }

    fun snapToTarget() {
        render.box = target
    }

    fun snapTo(box: Box) {
        target = box
        snapToTarget()
    }

    fun update(dt: Float = 0f) {

        render.box = when (MakkitClient.config.animations) {
            true -> Box(
                    (target.getStart() + render.box.getStart()).multiply(0.5),
                    (target.getEnd() + render.box.getEnd()).multiply(0.5)
            )
            false -> target
        }

    }

    fun draw() {
        update()
        render.draw()
    }

}