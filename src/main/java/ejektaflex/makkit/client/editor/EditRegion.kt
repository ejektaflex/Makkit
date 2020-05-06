package ejektaflex.makkit.client.editor

import ejektaflex.makkit.client.data.BoxTraceResult
import ejektaflex.makkit.client.editor.drag.tools.MoveToolDualAxis
import ejektaflex.makkit.client.editor.drag.tools.ResizeToolSingleAxis
import ejektaflex.makkit.client.editor.input.InputState
import ejektaflex.makkit.client.editor.input.MakkitKeys
import ejektaflex.makkit.client.render.RenderBox
import ejektaflex.makkit.client.render.RenderColor
import ejektaflex.makkit.common.network.pakkits.EditWorldPacket
import ejektaflex.makkit.common.world.WorldOperation
import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box

class EditRegion(var drawDragPlane: Boolean = false, var smoothDrag: Boolean = true) {

    val samplePlaneSize = 32.0

    val area = RenderBox().apply {
        fillColor = RenderColor.GREEN.toAlpha(.4f)
        edgeColor = RenderColor.DARK_GREEN.toAlpha(.2f)
    }

    val preview = RenderBox().apply {
        fillColor = RenderColor.BLUE.toAlpha(.4f)
        edgeColor = RenderColor.ORANGE.toAlpha(.2f)
    }

    private val moveToolDual = MoveToolDualAxis(this, MakkitKeys.moveDragBinding)
    private val resizeTool = ResizeToolSingleAxis(this, MakkitKeys.resizeSideBinding)
    //private val moveToolSingle = MoveToolSingleAxis(this, MakkitClient.moveDragSingleBinding)
    //private val resizeToolDual = ResizeToolDualAxis(this, ExampleMod.resizeDualSideBinding)

    private val tools = listOf(
            moveToolDual,
            resizeTool
            //moveToolSingle
            //resizeToolDual
    )

    fun moveTo(x: Int, y: Int, z: Int, sx: Int, sy: Int, sz: Int) {
        area.box = Box(BlockPos(x, y, z), BlockPos(x + sx, y + sy, z + sz))
    }

    fun update() {
        tools.forEach { tool -> tool.update() }
    }

    private fun trace(): BoxTraceResult? {
        return area.trace(InputState.isBackSelecting)
    }

    fun doOperation(operation: WorldOperation) {
        val trace = trace()
        if (trace != null) {
            EditWorldPacket(
                    BlockPos(area.pos),
                    BlockPos(area.end),
                    trace.dir,
                    operation,
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
                area.drawFace(it.dir, RenderColor.YELLOW.toAlpha(.3f))
                area.drawAxisSizes()
            }
        }
    }


}