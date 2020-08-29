package io.ejekta.makkit.client.render
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sqrt

enum class Easing(val func: (it: Double) -> Double) {
    InSine({
        1.0 - cos((it * PI) / 2.0)
    }),
    OutSine({
        sin((it * PI) / 2.0)
    }),

    InOutCirc({
        when {
            it < 0.5 -> 1.0 - sqrt(1.0 - (2 * it).pow(2.0))
            else -> sqrt(1.0 - (-2 * it + 2).pow(2.0)) + 1
        } / 2.0
    }),

    InCirc({
       1.0 - sqrt(1.0 - it.pow(2.0))
    }),

    // Why not?
    InElastic({
        when (it) {
            0.0, 1.0 -> it
            else -> -(2.0.pow(10 * it - 10)) * sin(it * 10 - 10.75) * EC4
        }
    });

    companion object {
        const val EC4 = (2 * PI) / 3
    }
}