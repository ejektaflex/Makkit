package io.ejekta.makkit.client.render

enum class Easing(val func: (it: Double) -> Double) {
    InSine({
        1.0 - kotlin.math.cos((it * kotlin.math.PI) / 2.0)
    })
}