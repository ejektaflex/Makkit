package io.ejekta.makkit.common.editor.data

import io.ejekta.makkit.common.editor.operations.PasteOperation
import io.ejekta.makkit.common.enum.UndoRedoMode
import io.ejekta.makkit.common.ext.*
import io.ejekta.makkit.common.network.pakkits.client.FocusRegionPacket
import net.minecraft.block.BlockState
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.LiteralText
import net.minecraft.util.BlockRotation
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

        val lookDir = face.opposite

        val d1: Vec3i = lookDir.rotateYClockwise().vector
        val d2: Vec3i = Direction.UP.vector
        val d3: Vec3i = lookDir.vector

        println("COPIED SIZE: ${CopyHelper.getCopyBoxSize(copyBox, face)}")

        val copyBoxSize: BlockPos = when (lookDir.axis) {
            Direction.Axis.Z -> BlockPos(copyBox.xLength, copyBox.yLength, copyBox.zLength)
            Direction.Axis.X -> BlockPos(copyBox.zLength, copyBox.yLength, copyBox.xLength)
            else -> throw Exception("This shouldn't happen!")
        }

        val startPos: BlockPos = when (lookDir) {
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

        // Copy data is ALWAYS in, say, NORTH direction
        copyData = CopyData(stateMap, copyBox, BlockPos(copyBoxSize), face)
        player.sendMessage(LiteralText("Copied data to clipboard!"), true)
    }

    fun paste(player: ServerPlayerEntity, pasteBox: Box, face: Direction) {

        // if we are looking north, the box is correct and we should just paste it
        //

        fun sizeRespectsDirection(size: Vec3d, forDir: Direction): Vec3d {
            return Vec3d(
                    size.axisValue(forDir.rotateYClockwise().axis),
                    size.y,
                    size.axisValue(forDir.axis)
            )
        }

        //*
        if (copyData != null) {
            val cd = copyData!!

            var ourSize = CopyHelper.getCopyBoxSize(pasteBox, face)

            val supposedSize = CopyHelper.getCopyBoxSize(cd.box, cd.dir)

            val flipped = BlockPos(supposedSize.z, supposedSize.y, supposedSize.x)

            // width, Y, depth

            if (ourSize != supposedSize) {
                println("Incorrect size! us: $ourSize, copy: $supposedSize")

                FocusRegionPacket(
                        Box(
                                pasteBox.startBlock(),
                                pasteBox.startBlock().add(flipped)
                        )
                ).sendToClient(player)

            }








            //FocusRegionPacket(newBox).sendToClient(player)

        }
        return
         //*/




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

                println("Incorrect rotation")

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