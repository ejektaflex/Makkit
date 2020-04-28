package ejektaflex.kalpis.edit

import ejektaflex.kalpis.ExampleMod
import ejektaflex.kalpis.data.BoxTraceResult
import ejektaflex.kalpis.edit.drag.Drag
import ejektaflex.kalpis.edit.planes.MovePlane
import ejektaflex.kalpis.edit.planes.SizePlane
import ejektaflex.kalpis.ext.flipMask
import ejektaflex.kalpis.ext.otherDirectionalAxes
import ejektaflex.kalpis.render.RenderBox
import ejektaflex.kalpis.render.RenderColor
import net.minecraft.block.Blocks
import net.minecraft.client.MinecraftClient
import net.minecraft.item.AirBlockItem
import net.minecraft.item.BlockItem
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

class EditRegion() {


    val region = RenderBox()

    val previewBlock = RenderBox()

    val moveDrag = Drag(this, ExampleMod.moveDragBinding)
    val stretchDrag = Drag(this, ExampleMod.stretchDragBinding)
    val shrinkDrag = Drag(this, ExampleMod.shrinkDragBinding)


    val movePlane = MovePlane(this)

    val sizePlane1 = SizePlane(this)
    val sizePlane2 = SizePlane(this)

    fun moveTo(x: Int, y: Int, z: Int, sx: Int, sy: Int, sz: Int) {
        region.box = Box(BlockPos(x, y, z), BlockPos(x + sx, y + sy, z + sz))
    }


    fun update() {
        movePlane.update()

        sizePlane1.update()
        sizePlane2.update()

        moveDrag.update()
        stretchDrag.update()
        shrinkDrag.update()

        if (ExampleMod.deleteBinding.isPressed) {

            println("Whoa")

            val blocks = region.getBlockArray()

            val mc = MinecraftClient.getInstance()
            val player = mc.player!!
            val item = player.mainHandStack.item


            if (item is BlockItem) {
                blocks.forEach { pos ->
                    mc.world!!.setBlockState(pos, item.block.defaultState)
                }
            } else if (item is AirBlockItem) {
                blocks.forEach { pos ->
                    mc.world!!.setBlockState(pos, Blocks.AIR.defaultState)
                }
            }


        }

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
            stretchDrag, shrinkDrag -> {

                println("Dragging size")

                val planes = listOf(sizePlane1, sizePlane2)

                val dirs = dragPoint.dir.otherDirectionalAxes()

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
                val box = movePlane.calcDragBox(drag, false)
                box?.let { region.box = it }
                println("move: $box")
            }
            stretchDrag, shrinkDrag -> {
                val box = sizePlane1.calcDragBox(drag, false, listOf(sizePlane2))
                box?.let { region.box = it }
                print("size: $box")
            }
        }

        println(previewBlock.box)

    }


    fun draw() {

        region.color = RenderColor.GREEN

        var showPlanes = true

        var smoothStep = true

        if (moveDrag.isDragging()) {
            previewBlock.box = movePlane.calcDragBox(moveDrag, smoothStep)
                    ?: previewBlock.box
            previewBlock.draw(RenderColor.BLUE)

            if (showPlanes) {
                movePlane.tryDraw()
            }

        }

        for (drag in listOf(stretchDrag, shrinkDrag)) {
            if (drag.isDragging()) {
                previewBlock.box = sizePlane1.calcDragBox(drag, smoothStep, otherPlanes = listOf(sizePlane2))
                        ?: previewBlock.box

                previewBlock.draw(RenderColor.BLUE)

                if (showPlanes) {
                    sizePlane1.tryDraw()
                    sizePlane2.tryDraw()
                }
            }
        }

        region.draw()

    }




}