package io.ejekta.makkit.client.editor

import io.ejekta.makkit.client.MakkitClient
import io.ejekta.makkit.client.MakkitClient.Companion.delta
import io.ejekta.makkit.client.MakkitClient.Companion.isInEditMode
import io.ejekta.makkit.client.MakkitClient.Companion.region
import io.ejekta.makkit.client.data.BoxTraceResult
import io.ejekta.makkit.client.editor.drag.DragTool
import io.ejekta.makkit.client.editor.drag.tools.MakkitTool
import io.ejekta.makkit.client.editor.handle.FaceHandle
import io.ejekta.makkit.client.editor.handle.Handle
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

    var hoveredHandle: Handle? = null

    var tool: DragTool? = null
    var toolEnumStored: MakkitTool? = null // TODO store enum in tool itself?

    fun startUsingTool(toolEnum: MakkitTool) {
        val toolMaker = toolEnum.producer

        val handle = region?.hoveredHandle ?: return

        if (isInEditMode) {
            tool = toolMaker(handle)
            toolEnumStored = toolEnum
        }
    }

    fun stopUsingTool(toolEnum: MakkitTool) {
        if (toolEnumStored == toolEnum) {
            tool!!.onStopDragging(tool!!.dragStart)
            tool = null
            toolEnumStored = null
        }
    }

    fun isActive() = MakkitClient.isInEditMode

    fun isBeingInteractedWith(): Boolean {
        return selection.trace() != BoxTraceResult.EMPTY
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
        FaceHandle(this, it)
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
            trace: BoxTraceResult = editBox.trace()
    ) {
        if (trace != BoxTraceResult.EMPTY) {
            EditWorldPacket(
                    editBox,
                    undoBox,
                    trace.dir,
                    operation,
                    EditWorldOptions().apply {
                        randomRotate = MakkitClient.randomRotate
                        weightedPalette = MakkitClient.weightedPalette
                        blockMask = MakkitClient.blockMask
                    },
                    ClientPalette.getSafePalette()
            ).sendToServer()
        }
    }

    fun draw() {
        renderSelection()

        // default state when no drag tool is being used
        val hit = selection.trace()

        val handle = getFaceHandle(hit.dir)

        tool?.let {
            it.update(delta)
            it.tryDraw()
            return
        }


        if (hit != BoxTraceResult.EMPTY) {

            handle.renderHover()
            hoveredHandle = handle

        } else {
            val camVec = MinecraftClient.getInstance().cameraEntity?.pos ?: return

            val backFaces = selectionRenderer.renderBox.genBackfacePlanes(9.0)

//                    for ((dir, bf) in backFaces) {
//                        RenderBox(bf).draw(RenderColor.BLUE.toAlpha(.3f))
//                    }

            val results = backFaces.map { it.key to it.value.trace() }.filter { it.second != BoxTraceResult.EMPTY }.toMap()

            val closestBackplane = results.minByOrNull { it.value.hit.distanceTo(
                camVec
            ) }?.key // if this is null, no reason to continue computation

            if (closestBackplane == null) {
                hoveredHandle = null
                return
            }

            closestBackplane.let {
                selectionRenderer.renderBox.drawFace(it, MakkitClient.selectionFaceColor.toAlpha(.3f))
                hoveredHandle = handle
            }
        }


    }


}