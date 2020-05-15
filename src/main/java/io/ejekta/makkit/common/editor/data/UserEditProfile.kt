package io.ejekta.makkit.common.editor.data

import io.ejekta.makkit.common.editor.operations.PasteOperation
import io.ejekta.makkit.common.enum.UndoRedoMode
import io.ejekta.makkit.common.ext.*
import io.ejekta.makkit.common.network.pakkits.client.FocusRegionPacket
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


    fun copy(player: ServerPlayerEntity, copyBox: Box, face: Direction) {

        if (face.axis == Direction.Axis.Y) {
            player.sendMessage(LiteralText("You have to look at a side face to copy a selection!"), true)
            return
        }

        val d1: Vec3i = face.rotateYClockwise().vector
        val d2: Vec3i = Direction.UP.vector
        val d3: Vec3i = face.vector

        println("COPIED SIZE: ${CopyHelper.getLocalAxisSize(copyBox, face)}")

        val copyBoxSize: BlockPos = CopyHelper.getLocalAxisSize(copyBox, face)

        val startPos: BlockPos = CopyHelper.getLocalAxisStartPos(copyBox, face)

        val stateMap = mutableMapOf<BlockPos, BlockState>()

        Box(BlockPos(0, 0, 0), BlockPos(copyBoxSize)).forEachBlockCoord { x, y, z ->
            stateMap[BlockPos(x, y, z)] = player.world.getBlockState(
                    startPos +
                            BlockPos(d1 * -z) +
                            BlockPos(d2 * y) +
                            BlockPos(d3 * -x)
            )
        }

        copyData = CopyData(stateMap, copyBox, BlockPos(copyBoxSize), face)
        player.sendMessage(LiteralText("Copied data to clipboard!"), true)
    }

    
    fun paste(player: ServerPlayerEntity, pasteBox: Box, face: Direction) {

        if (face.axis == Direction.Axis.Y) {
            player.sendMessage(LiteralText("You have to look at a side face to paste a selection!"), true)
            return
        }

        if (copyData != null) {
            val cd = copyData!!

            val ourSize = CopyHelper.getLocalAxisSize(pasteBox, face)

            val copiedSize = CopyHelper.getLocalAxisSize(cd.box, cd.dir)

            if (ourSize != copiedSize) {

                val toUse = if (face.axis == Direction.Axis.Z) {
                    ourSize
                } else {
                    copiedSize
                }

                val point = BlockPos(pasteBox.center)

                val useLo = toUse.vec3d().multiply(0.5).floor()
                val useHi = toUse.vec3d().multiply(0.5).ceil()

                val newBox = Box(
                        point.subtract(useLo),
                        point.add(useHi)
                )

                println("Incorrect size! us: $ourSize, copy: $copiedSize")

                FocusRegionPacket(
                        newBox
                ).sendToClient(player)

            } else {
                doAction(player, EditAction(
                        player,
                        pasteBox,
                        face,
                        PasteOperation(cd),
                        listOf()
                ))
            }

        }
        return

    }


    companion object {
        private const val maxHistLength = 50
    }

}