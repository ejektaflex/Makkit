package ejektaflex.kalpis.edit.planes

import ejektaflex.kalpis.edit.EditRegion
import ejektaflex.kalpis.edit.drag.Drag
import ejektaflex.kalpis.ext.round
import net.minecraft.util.math.Box

class MovePlane(region: EditRegion) : Plane(region) {

    override fun shouldDraw(): Boolean {
        return region.moveDrag.isDragging()
    }

    override fun calcDragBox(drag: Drag, smooth: Boolean, otherPlanes: List<Plane>): Box? {

        if (drag.isDragging()) {
            val offset = getDrawOffset(drag)

            if (offset != null) {

                val rounding = when (smooth) {
                    true -> offset
                    false -> offset.round()
                }

                return region.region.box.offset(rounding)
            }
        }

        return null
    }

}