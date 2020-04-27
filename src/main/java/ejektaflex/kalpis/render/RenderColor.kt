package ejektaflex.kalpis.render

import net.minecraft.client.util.math.Vector3f

enum class RenderColor(val r: Float, val g: Float, val b: Float, val a: Float = 1f) {
    WHITE(1f, 1f, 1f),
    RED(1f, 0f, 0f),
    ORANGE(1f, 0.5f, 0f),
    GREEN(0.2f, 1f, 0.1f),
    BLUE(0.4f, 0.9f, 0.9f),
    DARK_BLUE(0.3f, 0.4f, 0.9f);

    val floats: FloatArray by lazy {
        floatArrayOf(r, g, b, a)
    }

    fun toAlpha(inA: Float) = floatArrayOf(r, g, b, inA)



}