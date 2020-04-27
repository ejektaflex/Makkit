package ejektaflex.kalpis.edit

import ejektaflex.kalpis.ExampleMod
import ejektaflex.kalpis.data.BoxTraceResult
import ejektaflex.kalpis.edit.drag.Drag
import ejektaflex.kalpis.edit.planes.MovePlane
import ejektaflex.kalpis.edit.planes.SizePlane
import ejektaflex.kalpis.ext.flipMask
import ejektaflex.kalpis.ext.otherDirections
import ejektaflex.kalpis.render.RenderBox
import ejektaflex.kalpis.render.RenderColor
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

class EditRegion() {

    val region = BlockRegion()

    val blocksRender = RenderBox()

    val previewBlock = RenderBox()

    val moveDrag = Drag(this, ExampleMod.moveDragBinding)
    val sizeDrag = Drag(this, ExampleMod.sizeDragBinding)

    val movePlane = MovePlane(this)

    val sizePlane1 = SizePlane(this)
    val sizePlane2 = SizePlane(this)

    fun moveTo(x: Int, y: Int, z: Int, sx: Int, sy: Int, sz: Int) {
        region.pos = BlockPos(x, y, z)
        region.size = BlockPos(x + sx, y + sy, z + sz)
    }


    fun update() {
        blocksRender.fitTo(region)
        movePlane.update()

        sizePlane1.update()
        sizePlane2.update()

        moveDrag.update()
        sizeDrag.update()
    }


    fun onStartDragging(drag: Drag, dragPoint: BoxTraceResult) {

        when (drag) {
            moveDrag -> {
                println("Dragging move")
                val areaSize = Vec3d(12.0, 12.0, 12.0).flipMask(dragPoint.dir)

                movePlane.hitbox.box = Box(
                        dragPoint.hit.subtract(areaSize),
                        dragPoint.hit.add(areaSize)
                )
            }
            sizeDrag -> {

                println("Dragging size")

                val planes = listOf(sizePlane1, sizePlane2)

                val dirs = dragPoint.dir.otherDirections()

                dirs.forEachIndexed { i, direction ->

                    val areaSize = Vec3d(8.0, 8.0, 8.0).flipMask(direction)

                    planes[i].hitbox.box = Box(
                            dragPoint.hit.subtract(areaSize),
                            dragPoint.hit.add(areaSize)
                    )

                }

            }
        }

    }

    fun onStopDragging(drag: Drag, stopPoint: BoxTraceResult) {

        when (drag) {
            moveDrag -> {
                val box = movePlane.calcDragBox(drag)
                box?.let { region.fitTo(it) }
                println("move: $box")
            }
            sizeDrag -> {
                val box = sizePlane1.calcDragBox(drag, otherPlanes = listOf(sizePlane2))
                box?.let { region.fitTo(it) }
                print("size: $box")
            }
        }

        println(previewBlock.box)

    }


    fun draw() {

        blocksRender.color = RenderColor.GREEN

        //movePlane.tryDraw()
        //sizePlane1.tryDraw()
        //sizePlane2.tryDraw()

        if (moveDrag.isDragging()) {
            previewBlock.box = movePlane.calcDragBox(moveDrag)
                    ?: previewBlock.box
            previewBlock.draw(RenderColor.BLUE)
        }

        if (sizeDrag.isDragging()) {
            previewBlock.box = sizePlane1.calcDragBox(sizeDrag, otherPlanes = listOf(sizePlane2))
                    ?: previewBlock.box
            previewBlock.draw(RenderColor.BLUE)
        }



        //offset = sizePlane1.getDrawOffset()


        blocksRender.draw()

    }




}