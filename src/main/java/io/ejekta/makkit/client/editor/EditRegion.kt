package io.ejekta.makkit.client.editor

import io.ejekta.makkit.client.MakkitClient
import io.ejekta.makkit.client.data.BoxTraceResult
import io.ejekta.makkit.client.editor.drag.tools.*
import io.ejekta.makkit.client.editor.drag.tools.clipboard.CopyTool
import io.ejekta.makkit.client.editor.drag.tools.clipboard.PasteTool
import io.ejekta.makkit.client.editor.input.ClientPalette
import io.ejekta.makkit.client.render.RenderBox
import io.ejekta.makkit.client.render.RenderColor
import io.ejekta.makkit.common.editor.data.CopyHelper
import io.ejekta.makkit.common.editor.data.EditWorldOptions
import io.ejekta.makkit.common.editor.operations.WorldOperation
import io.ejekta.makkit.common.ext.*
import io.ejekta.makkit.common.network.pakkits.server.EditWorldPacket
import io.ejekta.makkit.common.network.pakkits.server.ShadowBoxUpdatePacket
import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d

class EditRegion(var drawDragPlane: Boolean = false) {

    var copyBox: Box? = null

    var selection: Box = Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
        set(value) {
            field = value
            ShadowBoxUpdatePacket(value).sendToServer()
        }

    val selectionRenderer = RenderBox().apply {
        fillColor = MakkitClient.config.selectionBoxColor.toAlpha(.4f)
        edgeColor = MakkitClient.config.selectionBoxColor.toAlpha(.4f)
    }

    fun renderSelection() {
        selectionRenderer.box = selection
        if (copyBox != null) {
            if (selection.blockSize() == CopyHelper.getLocalAxisSize(copyBox!!, Direction.NORTH)
                    || selection.blockSize() == CopyHelper.getLocalAxisSize(copyBox!!, Direction.EAST)) {
                selectionRenderer.edgeColor = RenderColor.PURPLE.toAlpha(.4f)
            } else {
                selectionRenderer.edgeColor = MakkitClient.config.selectionBoxColor.toAlpha(.4f)
            }
        } else {
            selectionRenderer.edgeColor = MakkitClient.config.selectionBoxColor.toAlpha(.4f)
        }

        selectionRenderer.draw()
    }

    private var tools = mutableListOf(
            MoveToolPlanar(this),
            ResizeToolAxial(this),
            ResizeToolSymmetric(this),
            PatternToolAxial(this),
            MirrorToolOpposite(this),
            CopyTool(this),
            PasteTool(this)
    )

    fun moveTo(x: Int, y: Int, z: Int, sx: Int, sy: Int, sz: Int) {
        selection = Box(BlockPos(x, y, z), BlockPos(x + sx, y + sy, z + sz))
    }

    fun centerOn(pos: BlockPos) {
        selection = Box(pos, pos.add(1, 1, 1))
    }

    fun tryScrollFace(amt: Double) {
        if (MinecraftClient.getInstance().world != null && MinecraftClient.getInstance().options.keySprint.isPressed) {
            val result = selection.autoTrace()

            if (result == BoxTraceResult.EMPTY) {
                return
            }

            val others = result.dir.otherAxisDirections()

            var boxProto = selection

            others.forEach { dir ->
                boxProto = boxProto.resizeBy(amt, dir)
            }

            selection = boxProto.withMinSize(Vec3d(1.0, 1.0, 1.0))
        }
    }

    fun update() {
        tools.forEach { tool -> tool.update() }
    }

    fun doOperation(
            operation: WorldOperation,
            editBox: Box = selection,
            undoBox: Box = editBox,
            trace: BoxTraceResult = editBox.autoTrace()
    ) {
        if (trace != BoxTraceResult.EMPTY) {
            EditWorldPacket(
                    editBox,
                    undoBox,
                    trace.dir,
                    operation,
                    EditWorldOptions().apply {
                        randomRotate = MakkitClient.config.randomRotate
                        weightedPalette = MakkitClient.config.weightedPalette
                    },
                    ClientPalette.getSafePalette()
            ).sendToServer()
        }
    }

    fun draw() {
        renderSelection()

        val anyToolsDragging = tools.any { it.isDragging() }

        if (anyToolsDragging) {
            tools.forEach { tool ->
                tool.update()
                tool.tryDraw()
            }
        } else {

            // default state when no drag tool is being used
            val hit = selection.autoTrace()
            if (hit != BoxTraceResult.EMPTY) {
                selectionRenderer.drawFace(hit.dir, MakkitClient.config.selectionFaceColor.toAlpha(.3f))
                selectionRenderer.drawAxisSizes()
            }
        }
    }


}