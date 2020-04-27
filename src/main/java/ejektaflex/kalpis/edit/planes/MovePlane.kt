package ejektaflex.kalpis.edit.planes

import ejektaflex.kalpis.data.BoxTraceResult
import ejektaflex.kalpis.edit.EditRegion
import ejektaflex.kalpis.edit.IEditor
import ejektaflex.kalpis.render.RenderBox
import ejektaflex.kalpis.render.RenderColor

class MovePlane(private val region: EditRegion) : IEditor {

    var hitbox = RenderBox()

    override fun shouldDraw(): Boolean {
        return region.isDragging
    }

    fun tryHit(): BoxTraceResult? {
        return hitbox.trace()
    }

    override fun update() {

    }

    override fun onDraw() {
        hitbox.draw(RenderColor.BLUE)
    }

}