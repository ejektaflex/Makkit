package ejektaflex.kalpis.client.editor

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