package ejektaflex.kalpis.render

class RenderColor(val r: Float, val g: Float, val b: Float, val a: Float = 1f) {


    val floats: FloatArray by lazy {
        floatArrayOf(r, g, b, a)
    }

    fun toAlpha(inA: Float) = RenderColor(r, g, b, inA)

    companion object {

        fun from(int: Int): RenderColor {
            //val a = (int shr 24 and 255).toFloat() / 255.0f
            val r = (int shr 16 and 255).toFloat() / 255.0f
            val g = (int shr 8 and 255).toFloat() / 255.0f
            val b = (int and 255).toFloat() / 255.0f

            return RenderColor(r, g, b, 1f)
        }

        val WHITE = from(0xFFFFFF)
        val RED = from(0xe43b44)
        val ORANGE = from(0xfeae34)
        val GREEN = from(0x63c74d)
        val BLUE = from(0x0099db)
        val DARK_BLUE = from(0x124e89)
        val PINK = from(0xf6757a)
    }


}