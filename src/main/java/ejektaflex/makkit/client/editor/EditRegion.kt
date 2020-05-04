package ejektaflex.makkit.client.editor

import ejektaflex.makkit.client.MakkitClient
import ejektaflex.makkit.client.data.BoxTraceResult
import ejektaflex.makkit.client.editor.drag.tools.MoveToolDualAxis
import ejektaflex.makkit.client.editor.drag.tools.MoveToolSingleAxis
import ejektaflex.makkit.client.editor.drag.tools.ResizeToolSingleAxis
import ejektaflex.makkit.client.editor.input.InputState
import ejektaflex.makkit.common.network.pakkits.EditIntentPacket
import ejektaflex.makkit.common.world.WorldOperation
import ejektaflex.makkit.client.render.MyLayers
import ejektaflex.makkit.client.render.RenderBox
import ejektaflex.makkit.client.render.RenderColor
import ejektaflex.makkit.client.render.RenderHelper
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.minecraft.block.Blocks
import net.minecraft.client.MinecraftClient
import net.minecraft.item.AirBlockItem
import net.minecraft.item.BlockItem
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box

class EditRegion(var drawDragPlane: Boolean = false, var smoothDrag: Boolean = true) {

    val samplePlaneSize = 16.0

    val area = RenderBox().apply {
        color = RenderColor.GREEN.toAlpha(0.4f)
    }

    val preview = RenderBox().apply {
        color = RenderColor.BLUE.toAlpha(0.4f)
    }

    private val moveToolDual = MoveToolDualAxis(this, MakkitClient.moveDragBinding)
    private val resizeTool = ResizeToolSingleAxis(this, MakkitClient.resizeSideBinding)
    private val moveToolSingle = MoveToolSingleAxis(this, MakkitClient.moveDragSingleBinding)
    //private val resizeToolDual = ResizeToolDualAxis(this, ExampleMod.resizeDualSideBinding)

    private val tools = listOf(
            moveToolDual,
            resizeTool,
            moveToolSingle//,
            //resizeToolDual
    )

    fun moveTo(x: Int, y: Int, z: Int, sx: Int, sy: Int, sz: Int) {
        area.box = Box(BlockPos(x, y, z), BlockPos(x + sx, y + sy, z + sz))
    }

    fun update() {
        tools.forEach { tool -> tool.update() }

        if (MakkitClient.deleteBinding.isDown) {

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

    private fun trace(): BoxTraceResult? {
        return area.trace(InputState.isBackSelecting)
    }

    fun doOperation(operation: WorldOperation) {
        val trace = trace()
        if (trace != null) {

            EditIntentPacket(
                    BlockPos(area.pos),
                    BlockPos(area.end),
                    trace.dir,
                    WorldOperation.FILL,
                    listOf(MinecraftClient.getInstance().player!!.mainHandStack)
            ).sendToServer()

        }
    }

    fun draw() {

        area.draw()

        val anyToolsDragging = tools.any { it.isDragging() }

        if (anyToolsDragging) {
            tools.forEach { tool ->
                tool.update()
                tool.tryDraw()
            }
        } else {
            // default state when no drag tool is being used
            val hit = trace()
            hit?.let {
                area.drawFace(it.dir, RenderColor.YELLOW.toAlpha(.4f))
                area.drawAxisSizes()
            }

            for (pos in area.getBlockArray()) {
                if (MinecraftClient.getInstance().world!!.getBlockState(pos).isAir) {
                    RenderHelper.drawBlockFaces(pos, RenderColor.ORANGE.toAlpha(.2f), MyLayers.OVERLAY_QUADS_BEHIND)
                }
            }

        }

    }


}