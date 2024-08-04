package io.ejekta.makkit.client.editor

import io.ejekta.makkit.client.MakkitClient
import io.ejekta.makkit.client.MakkitClient.Companion.timeDelta
import io.ejekta.makkit.client.data.BoxTraceResult
import io.ejekta.makkit.client.editor.drag.tools.MakkitTool
import io.ejekta.makkit.client.editor.input.ClientPalette
import io.ejekta.makkit.client.render.AnimBox
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

    inner class HandleContext(
        val handle: Handle,
        val hit: Vec3d
    )

    inner class ToolUsageContext(
        toolEnum: MakkitTool,
        val hoverContext: HandleContext
    ) {
        val region: EditRegion = this@EditRegion
        private val tool = toolEnum.producer(hoverContext)

        fun startUsing() {
            tool.onStartDragging()
        }
        fun stopUsing() {
            tool.onStopDragging()
        }
        fun updateAndDraw() {
            tool.let {
                it.update(timeDelta)
                it.tryDraw()
            }
        }
    }

    var hoverContext: HandleContext? = null
    var toolContext: ToolUsageContext? = null

    var copyBox: Box? = null

    var selection: Box = Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
        set(value) {
            field = value
            ShadowBoxUpdatePacket(value).sendToServer()
        }

    val colorToDraw = MakkitClient.selectionBoxColor.toAlpha(.4f)

    val selectionRenderer = AnimBox({ selection }) {
        draw(colorToDraw)
    }

    fun startUsingTool(toolEnum: MakkitTool) {
        hoverContext?.let {
            stopUsingTool()
            toolContext = ToolUsageContext(toolEnum, it).also { ctx ->
                ctx.startUsing()
            }
        }
    }

    fun stopUsingTool() {
        toolContext?.stopUsing()
        toolContext = null
    }

    //fun isActive() = MakkitClient.isInEditMode

    fun isBeingInteractedWith(): Boolean {
        return hoverContext != null
    }

    private fun renderSelection() {
        selectionRenderer.renderBox.draw(colorFill = getSelectionColor(), colorEdge = getSelectionColor())
    }

    private fun getSelectionColor(): RenderColor {

        val default = MakkitClient.selectionBoxColor.toAlpha(.4f)

        return if (MakkitClient.isInEditMode) {
            if (copyBox == null) {
                default
            } else {
                val isPasteSize = selection.blockSize() == CopyHelper.getLocalAxisSize(copyBox!!, Direction.NORTH)
                        || selection.blockSize() == CopyHelper.getLocalAxisSize(copyBox!!, Direction.EAST)
                if (isPasteSize) {
                    MakkitClient.pasteBoxColor.toAlpha(.4f)
                } else {
                    default
                }
            }
        } else {
            RenderColor.WHITE.toAlpha(.08f)
        }
    }

    private var handles = Direction.entries.associateWith {
        Handle(this, it)
    }

    fun getFaceHandle(dir: Direction): Handle {
        return handles[dir]!!
    }

    fun moveTo(x: Int, y: Int, z: Int, sx: Int, sy: Int, sz: Int) {
        selection = Box.enclosing(BlockPos(x, y, z), BlockPos(x + sx, y + sy, z + sz))
    }

    fun centerOriginCubeOn(pos: BlockPos) {
        selection = Box.enclosing(pos, pos.add(1, 1, 1))
        //selectionRenderer.shrinkToCenter()
    }

    fun centerOn(pos: BlockPos) {
        val half =  BlockPos(selection.getSize().multiply(0.5).roundToVec3i())
        selection = Box.enclosing(pos.subtract(half), pos.add(half)).withMinSize(Vec3d(1.0, 1.0, 1.0))
        selectionRenderer.shrinkToCenter()
    }

    fun update(delta: Long) {
        selectionRenderer.update(delta)
    }

    fun doOperation(
            operation: WorldOperation,
            editBox: Box = selection,
            undoBox: Box = editBox,
            direction: Direction = Direction.UP
    ) {
        EditWorldPacket(
            editBox,
            undoBox,
            direction,
            operation,
            EditWorldOptions().apply {
                randomRotate = MakkitClient.randomRotate
                weightedPalette = MakkitClient.weightedPalette
                blockMask = MakkitClient.blockMask
            },
            ClientPalette.getSafePalette().map { it.id }
        ).sendToServer()
    }

    fun draw() {
        renderSelection()

        // default state when no drag tool is being used
        val hit = selection.trace()

        val handle = getFaceHandle(hit.dir)

        toolContext?.let {
            it.updateAndDraw()
            return
        }

        if (hit != BoxTraceResult.EMPTY) {
            handle.renderHover()
            hoverContext = HandleContext(handle, hit.hit)
        } else {
            val camVec = MinecraftClient.getInstance().cameraEntity?.pos ?: return

            val backFaces = selectionRenderer.renderBox.genBackfacePlanes(camVec, 1.0)


//            for ((dir, bf) in backFaces) {
//                bf.draw(RenderColor.BLUE.toAlpha(.3f))
//            }

            val results = backFaces.map { it.first to it.second.trace() }.filter { it.second != BoxTraceResult.EMPTY }.toMap()

            // Compute the closest backplane
            val closestBackplane = results.minByOrNull { it.value.hit.distanceTo(
                camVec
            ) }

            if (closestBackplane == null) {
                hoverContext = null
                return
            }

            closestBackplane.let {
                selectionRenderer.renderBox.drawFace(it.key, MakkitClient.selectionFaceColor.toAlpha(.3f))
                hoverContext = HandleContext(Handle(this, closestBackplane.key), it.value.hit)
            }
        }


    }


}