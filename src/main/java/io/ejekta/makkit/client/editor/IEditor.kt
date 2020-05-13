package io.ejekta.makkit.client.editor

interface IEditor {

    fun update()

    fun shouldDraw(): Boolean {
        return true
    }

    fun onDrawPreview() {

    }

    fun tryDraw() {
        if (shouldDraw()) {
            onDrawPreview()
        }
    }

}