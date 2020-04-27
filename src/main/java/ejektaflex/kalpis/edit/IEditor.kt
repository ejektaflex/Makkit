package ejektaflex.kalpis.edit

interface IEditor {

    fun update()

    fun shouldDraw(): Boolean {
        return true
    }

    fun onDraw() {

    }

    fun tryDraw() {
        if (shouldDraw()) {
            onDraw()
        }
    }

}