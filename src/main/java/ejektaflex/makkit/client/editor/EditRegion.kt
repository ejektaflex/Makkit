package ejektaflex.makkit.client.editor

import ejektaflex.makkit.client.data.BoxTraceResult
import ejektaflex.makkit.client.editor.drag.tools.MoveToolDualAxis
import ejektaflex.makkit.client.editor.drag.tools.RepeatPatternTool
import ejektaflex.makkit.client.editor.drag.tools.ResizeToolSingleAxis
import ejektaflex.makkit.client.editor.drag.tools.ResizeToolSymmetric
import ejektaflex.makkit.client.editor.input.InputState
import ejektaflex.makkit.client.editor.input.MakkitKeys
import ejektaflex.makkit.client.render.RenderBox
import ejektaflex.makkit.client.render.RenderColor
import ejektaflex.makkit.common.ext.*
import ejektaflex.makkit.common.network.pakkits.server.EditWorldPacket
import ejektaflex.makkit.common.editor.operations.WorldOperation
import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

class EditRegion(var drawDragPlane: Boolean = false) {

    val area = RenderBox().apply {
        fillColor = RenderColor.GREEN.toAlpha(.4f)
        edgeColor = RenderColor.GREEN
    }

    val preview = RenderBox().apply {
        fillColor = RenderColor.BLUE.toAlpha(.4f)
        edgeColor = RenderColor.ORANGE.toAlpha(.2f)
    }

    private val moveToolDual = MoveToolDualAxis(this, MakkitKeys.moveDragBinding)
    private val resizeTool = ResizeToolSingleAxis(this, MakkitKeys.resizeSideBinding)
    private val resizeToolSymmetric = ResizeToolSymmetric(this, MakkitKeys.resizeSymmetricBinding)
    private val repeatPatternTool = RepeatPatternTool(this, MakkitKeys.repeatPatternBinding)

    private val tools = listOf(
            moveToolDual,
            resizeTool,
            resizeToolSymmetric,
            repeatPatternTool
    )

    fun moveTo(x: Int, y: Int, z: Int, sx: Int, sy: Int, sz: Int) {
        area.box = Box(BlockPos(x, y, z), BlockPos(x + sx, y + sy, z + sz))
    }

    fun centerOn(pos: BlockPos) {
        area.box = Box(pos, pos.add(1, 1, 1))
    }

    fun tryScrollFace(amt: Double) {
        if (MinecraftClient.getInstance().world != null && MinecraftClient.getInstance().options.keySprint.isPressed) {
            val result = trace()

            if (result == BoxTraceResult.EMPTY) {
                return
            }

            val others = result.dir.otherAxisDirections()

            var boxProto = area.box

            others.forEach { dir ->
                boxProto = boxProto.resizeBy(amt, dir)
            }

            area.box = boxProto.withMinSize(Vec3d(1.0, 1.0, 1.0))
        }
    }

    fun update() {
        tools.forEach { tool -> tool.update() }
    }

    private fun trace(): BoxTraceResult {
        return area.trace(InputState.isBackSelecting)
    }

    fun doOperation(operation: WorldOperation) {
        val trace = trace()
        if (trace != BoxTraceResult.EMPTY) {
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
            if (hit != BoxTraceResult.EMPTY) {
                area.drawFace(hit.dir, RenderColor.YELLOW.toAlpha(.3f))
                area.drawAxisSizes()
            }
        }
    }


}