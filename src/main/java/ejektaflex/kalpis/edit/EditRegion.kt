package ejektaflex.kalpis.edit

import ejektaflex.kalpis.ExampleMod
import ejektaflex.kalpis.edit.drag.tools.DualAxisMoveTool
import ejektaflex.kalpis.edit.drag.tools.SingleAxisResizeTool
import ejektaflex.kalpis.render.RenderBox
import ejektaflex.kalpis.render.RenderColor
import ejektaflex.kalpis.render.RenderHelper
import net.minecraft.block.Blocks
import net.minecraft.client.MinecraftClient
import net.minecraft.item.AirBlockItem
import net.minecraft.item.BlockItem
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box

class EditRegion() {


    val area = RenderBox().apply {
        color = RenderColor.GREEN
    }

    val preview = RenderBox().apply {
        color = RenderColor.BLUE
    }

    private val moveTool = DualAxisMoveTool(this, ExampleMod.moveDragBinding)
    private val resizeTool = SingleAxisResizeTool(this, ExampleMod.resizeSideBinding)

    private val tools = listOf(
            moveTool,
            resizeTool
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

        if (tools.any { it.isDragging() }) {
            tools.forEach { tool -> tool.update() }
            preview.draw()
        }

        tools.find { it.isDragging() }?.tryDraw()

        RenderHelper.drawText(area.box.center.add(0.0, area.box.yLength / 2, 0.0), "Hello!")

    }




}