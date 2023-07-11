package io.ejekta.makkit.client.editor

import io.ejekta.makkit.client.MakkitClient
import io.ejekta.makkit.client.data.BoxTraceResult
import io.ejekta.makkit.client.editor.drag.tools.*
import io.ejekta.makkit.client.editor.drag.tools.clipboard.CopyTool
import io.ejekta.makkit.client.editor.drag.tools.clipboard.PasteTool
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
            selectionRenderer.resize(value)
            ShadowBoxUpdatePacket(value).sendToServer()
        }

    val selectionRenderer = AnimBox(selection) {
        fillColor = MakkitClient.selectionBoxColor.toAlpha(.4f)
        edgeColor = MakkitClient.selectionBoxColor.toAlpha(.4f)
    }

    fun changeColors(fill: RenderColor, edge: RenderColor = fill) {
        selectionRenderer.render.fillColor = fill
        selectionRenderer.render.edgeColor = edge
    }

    fun isActive() = MakkitClient.isInEditMode

    fun isBeingInteractedWith(): Boolean {
        return isAnyToolBeingUsed() || selection.autoTrace() != BoxTraceResult.EMPTY
    }

    fun isAnyToolBeingUsed(): Boolean {
        return tools.any { it.isDragging() }
    }

    fun renderSelection() {
        selectionRenderer.render.draw(colorFill = getSelectionColor(), colorEdge = getSelectionColor())
    }

    fun getSelectionColor(): RenderColor {

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

    private var tools = mutableListOf(
            MoveToolPlanar(this),
            MoveToolAxial(this),
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

    fun centerOriginCubeOn(pos: BlockPos) {
        selection = Box(pos, pos.add(1, 1, 1))
        //selectionRenderer.shrinkToCenter()
    }

    fun centerOn(pos: BlockPos) {
        val half =  BlockPos(selection.getSize().multiply(0.5).roundToVec3i())
        selection = Box(pos.subtract(half), pos.add(half)).withMinSize(Vec3d(1.0, 1.0, 1.0))
        selectionRenderer.shrinkToCenter()
    }

    @Deprecated("Scrolling on faces may make a future return, but not quite like this")
    fun tryScrollFace(amt: Double) {
        if (MinecraftClient.getInstance().world != null && MinecraftClient.getInstance().options.sprintKey.isPressed) {
            val result = selection.autoTrace()

            if (result == BoxTraceResult.EMPTY) {
                return
            }

            val others = result.dir.alternateAxesDirs()

            var boxProto = selection

            others.forEach { dir ->
                boxProto = boxProto.resizeBy(amt, dir)
            }

            selection = boxProto.withMinSize(Vec3d(1.0, 1.0, 1.0))
        }
    }

    fun update(delta: Long) {
        selectionRenderer.update(delta)
        tools.forEach { tool -> tool.update(delta) }
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

        if (MakkitClient.isInEditMode) {
            if (isAnyToolBeingUsed()) {
                tools.forEach { tool ->
                    //tool.update(delta)
                    tool.tryDraw()
                }
            } else {

                // default state when no drag tool is being used
                val hit = selection.autoTrace()
                if (hit != BoxTraceResult.EMPTY) {
                    selectionRenderer.render.drawFace(hit.dir, MakkitClient.selectionFaceColor.toAlpha(.3f))
                    selectionRenderer.render.drawAxisSizes()
                } else {
                    val camVec = MinecraftClient.getInstance().cameraEntity?.pos ?: return

                    val backFaces = selectionRenderer.render.genBackfacePlanes(9.0)

//                    for ((dir, bf) in backFaces) {
//                        RenderBox(bf).draw(RenderColor.BLUE.toAlpha(.3f))
//                    }

                    val results = backFaces.map { it.key to it.value.autoTrace() }.filter { it.second != BoxTraceResult.EMPTY }.toMap()

                    val closestBackplane = results.minByOrNull { it.value.hit.distanceTo(
                        camVec
                    ) }?.key

                    closestBackplane?.let {
                        selectionRenderer.render.drawFace(it, MakkitClient.selectionFaceColor.toAlpha(.3f))
                    }
                }



            }
        }


    }


}