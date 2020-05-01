package ejektaflex.kalpis.render

data class RenderColor(val value: Long) : Cloneable {

    var a = (value shr 24 and 255).toFloat() / 255.0f
        private set

    val r = (value shr 16 and 255).toFloat() / 255.0f
    val g = (value shr 8 and 255).toFloat() / 255.0f
    val b = (value and 255).toFloat() / 255.0f

    constructor(int: Int) : this(0xFF000000 + int)

    //constructor(int: Int, alpha: Int) : this(int + (alpha.toLong()) shl 24)

    val intValue: Int
        get() = value.toInt()

    val floats: FloatArray by lazy {
        floatArrayOf(r, g, b, a)
    }

    fun toAlpha(inA: Float): RenderColor = (clone() as RenderColor).apply {
        a = inA
    }

    companion object {
        val WHITE = RenderColor(0xFFFFFF)
        val RED = RenderColor(0xe43b44)
        val ORANGE = RenderColor(0xfeae34)
        val GREEN = RenderColor(0x63c74d)
        val BLUE = RenderColor(0x0099db)
        val DARK_BLUE = RenderColor(0x124e89)
        val PINK = RenderColor(0xf6757a)
        val YELLOW = RenderColor(0xfee761)
        val PURPLE = RenderColor(0xb55088)
        val DARK_PURPLE = RenderColor(0x68386c)
    }


}