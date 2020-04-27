package ejektaflex.kalpis.edit.planes

import ejektaflex.kalpis.data.BoxTraceResult
import ejektaflex.kalpis.edit.EditRegion
import ejektaflex.kalpis.edit.ICanHit
import ejektaflex.kalpis.edit.IEditor
import ejektaflex.kalpis.edit.drag.Drag
import ejektaflex.kalpis.ext.dirMask
import ejektaflex.kalpis.ext.flipMask
import ejektaflex.kalpis.render.RenderBox
import ejektaflex.kalpis.render.RenderColor
import net.minecraft.util.math.Vec3d

class SizePlane(private val region: EditRegion) : IEditor, ICanHit {

    override var hitbox = RenderBox()

    override fun shouldDraw(): Boolean {
        return region.sizeDrag.isDragging()
    }

    fun tryHit(): BoxTraceResult? {
        return hitbox.trace()
    }

    override fun update() {

    }

    fun getDrawOffset(drag: Drag): Vec3d? {
        val start = drag.start
        val current = hitbox.trace()
        if (start != null && current != null) {
            return current.hit.subtract(start.hit).dirMask(start.dir)
        }
        return null
    }

    override fun onDraw() {
        hitbox.draw(RenderColor.BLUE)
    }

}