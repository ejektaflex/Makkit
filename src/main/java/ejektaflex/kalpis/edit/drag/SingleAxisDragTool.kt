package ejektaflex.kalpis.edit.drag

import ejektaflex.kalpis.data.BoxTraceResult
import ejektaflex.kalpis.edit.EditRegion
import ejektaflex.kalpis.ext.flipMask
import ejektaflex.kalpis.ext.otherDirectionalAxes
import ejektaflex.kalpis.render.RenderBox
import ejektaflex.kalpis.render.RenderColor
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

internal abstract class SingleAxisDragTool(region: EditRegion, binding: FabricKeyBinding) : DragTool(region,binding) {

    protected val planeAxis1 = RenderBox()
    protected val planeAxis2 = RenderBox()

    val planes: List<RenderBox>
        get() = listOf(planeAxis1, planeAxis2)


    override fun onStartDragging(start: BoxTraceResult) {
        println("Dragging size")

        val renderPlanes = planes

        val dirs = start.dir.otherDirectionalAxes()

        dirs.forEachIndexed { i, direction ->
            val areaSize = Vec3d(
                    region.samplePlaneSize,
                    region.samplePlaneSize,
                    region.samplePlaneSize
            ).flipMask(direction)
            renderPlanes[i].box = Box(
                    start.hit.subtract(areaSize),
                    start.hit.add(areaSize)
            )
        }
    }

    override fun onStopDragging(stop: BoxTraceResult) {
        val box = calcDragBox(false)
        box?.let { region.area.box = it }
    }

     override fun onDraw() {
         if (isDragging()) {
             region.preview.box = calcDragBox(region.smoothDrag) ?: region.preview.box

             if (region.drawDragPlane) {
                 planeAxis1.draw(RenderColor.PINK.toAlpha(0.2f))
                 planeAxis2.draw(RenderColor.PINK.toAlpha(0.2f))
             }

         }
     }

}