package io.ejekta.makkit.client.editor

import io.ejekta.makkit.client.data.BoxTraceResult
import io.ejekta.makkit.client.editor.drag.tools.MirrorToolOpposite
import io.ejekta.makkit.client.editor.drag.tools.MoveToolPlanar
import io.ejekta.makkit.client.editor.drag.tools.PatternToolAxial
import io.ejekta.makkit.client.editor.drag.tools.ResizeToolAxial
import io.ejekta.makkit.client.editor.drag.tools.ResizeToolSymmetric
import io.ejekta.makkit.client.editor.drag.tools.clipboard.CopyTool
import io.ejekta.makkit.client.editor.drag.tools.clipboard.PasteTool
import io.ejekta.makkit.client.editor.input.InputState
import io.ejekta.makkit.client.editor.input.MakkitKeys
import io.ejekta.makkit.client.render.RenderBox
import io.ejekta.makkit.client.render.RenderColor
import io.ejekta.makkit.common.ext.*
import io.ejekta.makkit.common.network.pakkits.server.EditWorldPacket
import io.ejekta.makkit.common.editor.operations.WorldOperation
import io.ejekta.makkit.common.network.pakkits.server.ShadowBoxUpdatePacket
import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

class EditRegion(var drawDragPlane: Boolean = false) {

    val area = RenderBox().apply {
        fillColor = RenderColor.GREEN.toAlpha(.4f)
        edgeColor = RenderColor.GREEN
    }

    fun setArea(box: Box) {
        area.box = box
        ShadowBoxUpdatePacket(box).sendToServer()
    }

    private var tools = mutableListOf(
            MoveToolPlanar(this, MakkitKeys.moveDragBinding),
            ResizeToolAxial(this, MakkitKeys.resizeSideBinding),
            ResizeToolSymmetric(this, MakkitKeys.resizeSymmetricBinding),
            PatternToolAxial(this, MakkitKeys.repeatPatternBinding),
            MirrorToolOpposite(this, MakkitKeys.mirrorToolBinding),
            CopyTool(this, MakkitKeys.copyBinding),
            PasteTool(this, MakkitKeys.pasteBinding)
    )

    fun moveTo(x: Int, y: Int, z: Int, sx: Int, sy: Int, sz: Int) {
        setArea(Box(BlockPos(x, y, z), BlockPos(x + sx, y + sy, z + sz)))
    }

    fun centerOn(pos: BlockPos) {
        setArea(Box(pos, pos.add(1, 1, 1)))
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

            setArea(boxProto.withMinSize(Vec3d(1.0, 1.0, 1.0)))
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
                    area.box,
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