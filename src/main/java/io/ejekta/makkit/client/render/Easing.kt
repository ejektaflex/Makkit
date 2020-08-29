package io.ejekta.makkit.client.render
import kotlin.math.cos
import kotlin.math.PI

enum class Easing(val func: (it: Double) -> Double) {
    InSine({
        1.0 - cos((it * PI) / 2.0)
    })
}