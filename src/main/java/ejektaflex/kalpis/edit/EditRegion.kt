package ejektaflex.kalpis.edit

import ejektaflex.kalpis.ExampleMod
import ejektaflex.kalpis.edit.drag.tools.MoveToolsDualAxis
import ejektaflex.kalpis.edit.drag.tools.ResizeToolSingleAxis
import ejektaflex.kalpis.render.RenderBox
import ejektaflex.kalpis.render.RenderColor
import ejektaflex.kalpis.render.RenderHelper
import net.minecraft.block.Blocks
import net.minecraft.client.MinecraftClient
import net.minecraft.item.AirBlockItem
import net.minecraft.item.BlockItem
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box

class EditRegion {


    val area = RenderBox().apply {
        color = RenderColor.GREEN
    }

    val preview = RenderBox().apply {
        color = RenderColor.BLUE
    }

    private val moveTool = MoveToolsDualAxis(this, ExampleMod.moveDragBinding)
    private val resizeTool = ResizeToolSingleAxis(this, ExampleMod.resizeSideBinding)
    private val resizeToolOpp = ResizeToolSingleAxis(this, ExampleMod.resizeOppSideBinding, true)

    private val tools = listOf(
            moveTool,
            resizeTool,
            resizeToolOpp
    )

    fun moveTo(x: Int, y: Int, z: Int, sx: Int, sy: Int, sz: Int) {
        area.box = Box(BlockPos(x, y, z), BlockPos(x + sx, y + sy, z + sz))
    }

    fun update() {
        tools.forEach { tool -> tool.update() }

        if (ExampleMod.deleteBinding.isPressed) {

            println("Whoa")

            val blocks = area.getBlockArray()

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

    fun draw() {

        area.draw()

        val anyToolsDragging = tools.any { it.isDragging() }

        if (anyToolsDragging) {
            tools.forEach { tool -> tool.update() }
            preview.draw()
        } else {
            val hit = area.trace()
            hit?.let {
                area.drawFace(it.dir)
                area.drawAxisNumbers()
                //area.drawDimensions(it.dir)
            }
        }


        tools.find { it.isDragging() }?.tryDraw()

        val dirsPretty = RenderHelper.getLookDirections().joinToString(",") { it.toString() }
        //RenderHelper.drawText(preview.box.center.add(0.0, preview.box.yLength / 2, 0.0), dirsPretty)


    }


}