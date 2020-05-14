package io.ejekta.makkit.common.editor.data

import io.ejekta.makkit.client.render.RenderHelper
import io.ejekta.makkit.common.editor.operations.PasteOperation
import io.ejekta.makkit.common.enum.UndoRedoMode
import io.ejekta.makkit.common.ext.*
import io.ejekta.makkit.common.network.pakkits.client.FocusRegionPacket
import jdk.nashorn.internal.ir.Block
import net.minecraft.block.BlockState
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.LiteralText
import net.minecraft.util.math.*
import kotlin.math.abs
import java.util.*
import kotlin.math.sign

/**
    Represents data about a player and their edits etc in the world
 */
class UserEditProfile {

    private var undoHistory = ArrayDeque<EditAction>()
    private var redoHistory = ArrayDeque<EditAction>()

    private var copyData: CopyData? = null


    fun doAction(player: ServerPlayerEntity, action: EditAction) {
        action.doInitialCommit(player)
        addToHistory(action)
    }

    fun clearHistory(): Boolean {
        undoHistory.clear()
        redoHistory.clear()
        return true
    }

    fun undo(player: ServerPlayerEntity): Boolean {
        return if (undoHistory.isEmpty()) {
            false
        } else {
            undoHistory.pop().let {
                it.syncToWorldState(UndoRedoMode.UNDO)
                it.revertCommit(player.world)
                it.select(player)
                redoHistory.push(it)
            }
            true
        }
    }

    fun redo(player: ServerPlayerEntity): Boolean {
        return if (redoHistory.isEmpty()) {
            false
        } else {
            redoHistory.pop().let {
                it.syncToWorldState(UndoRedoMode.REDO)
                it.commit(player.world)
                it.select(player)
                undoHistory.push(it)
            }
            true
        }
    }

    fun addToHistory(action: EditAction) {

        if (undoHistory.isNotEmpty()) {
            undoHistory.peek().syncToWorldState(UndoRedoMode.UNDO)
        }

        undoHistory.push(action)
        if (undoHistory.size > maxHistLength) {
            undoHistory.removeLast()
        }
        redoHistory.clear()
    }

    fun getLookCardinalDirection(look: Vec3d): Direction {
        // we don't care about up or down.
        return if (abs(look.x) > abs(look.z)) {
            Direction.fromVector(sign(look.x).toInt(), 0, 0)!!
        } else {
            Direction.fromVector(0, 0, sign(look.z).toInt())!!
        }
    }

    /**
     * Copied state is relative to box start
     */
    fun copy(player: ServerPlayerEntity, copyBox: Box, face: Direction) {
        val look: Vec3d = RenderHelper.getLookVector() // TODO: Actually get this

        val lookDir = getLookCardinalDirection(look)

        val d1: Vec3i = lookDir.rotateYClockwise().vector
        val d2: Vec3i = Direction.UP.vector
        val d3: Vec3i = lookDir.vector
        val copyBoxSize: Vec3i
        val startPos: BlockPos

        copyBoxSize = when (lookDir.axis) {
            Direction.Axis.Z -> Vec3i(copyBox.xLength, copyBox.yLength, copyBox.zLength)
            Direction.Axis.X -> Vec3i(copyBox.zLength, copyBox.yLength, copyBox.xLength)
            else -> throw Exception("This shouldn't happen!")
        }

        startPos = when (lookDir) {
            Direction.NORTH -> BlockPos(copyBox.x1, copyBox.y1, copyBox.z2 - 1)
            Direction.EAST -> BlockPos(copyBox.x1, copyBox.y1, copyBox.z1)
            Direction.SOUTH -> BlockPos(copyBox.x2 - 1, copyBox.y1, copyBox.z1)
            Direction.WEST -> BlockPos(copyBox.x2 - 1, copyBox.y1, copyBox.z2 - 1)
            else -> throw Exception("Cannot paste when look vector is up or down")
        }

        val stateMap = mutableMapOf<BlockPos, BlockState>()

        Box(BlockPos(0, 0, 0), BlockPos(copyBoxSize)).forEachBlockCoord { x, y, z ->
            stateMap[BlockPos(x, y, z)] = player.world.getBlockState(
                    startPos +
                            BlockPos(d1 * x) +
                            BlockPos(d2 * y) +
                            BlockPos(d3 * z)
            )
        }

        copyData = CopyData(stateMap, copyBox, face)
        player.sendMessage(LiteralText("Copied data to clipboard!"), true)
    }

    fun paste(player: ServerPlayerEntity, pasteBox: Box, face: Direction) {




        if (copyData != null) {
            val copy = copyData!!

            val oldSize = copy.box.getSize()
            val rotatedSize = Vec3d(oldSize.z, oldSize.y, oldSize.x) // flip X and Z

            if (pasteBox.getSize() == copy.box.getSize()) { // size is the same, don't care about axis yet

                println("Pasting")

                doAction(player, EditAction(
                        player,
                        pasteBox,
                        face,
                        PasteOperation(copy, otherAxis = false),
                        listOf()
                ))

            } else if (pasteBox.getSize() == rotatedSize) {

                println("Well it's rotated sideways alright")

                doAction(player, EditAction(
                        player,
                        pasteBox,
                        face,
                        PasteOperation(copy, otherAxis = true),
                        listOf()
                ))

            } else {
                FocusRegionPacket(Box(
                        pasteBox.getStart(),
                        pasteBox.getStart().add(rotatedSize)
                )).sendToClient(player)
            }

        } else {
            player.sendMessage(LiteralText("Can't paste, you haven't copied anything"), true)
        }

    }


    companion object {
        private const val maxHistLength = 50
    }

}