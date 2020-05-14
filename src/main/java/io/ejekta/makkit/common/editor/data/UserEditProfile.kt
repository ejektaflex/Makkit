package io.ejekta.makkit.common.editor.data

import io.ejekta.makkit.client.render.RenderHelper
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
import kotlin.math.absoluteValue
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

        val copyBoxSize: BlockPos = CopyHelper.getCopyBoxSize(copyBox, lookDir)

        val startPos: BlockPos = CopyHelper.getCopyBoxPos(copyBox, lookDir)

        val stateMap = mutableMapOf<BlockPos, BlockState>()

        Box(BlockPos(0, 0, 0), BlockPos(copyBoxSize)).forEachBlockCoord { x, y, z ->
            stateMap[BlockPos(z, y, x)] = player.world.getBlockState(
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


        //*
        if (copyData != null) {
            val cd = copyData!!


            // getCopyBox should become getLocalAxisSize
            var ourSize = CopyHelper.getCopyBoxSize(pasteBox, face)

            val supposedSize = CopyHelper.getCopyBoxSize(cd.box, cd.dir)


            var toUse = if (face.axis == Direction.Axis.Z) {
                ourSize
            } else {
                supposedSize
            }

            //val point = pasteBox.getFacePlane(face).resizeBy(toUse.vec3d().axisValue(face.axis).absoluteValue, face).center
            val point = BlockPos(pasteBox.center)

            val useLo = toUse.vec3d().multiply(0.5).floor()
            val useHi = toUse.vec3d().multiply(0.5).ceil()

            val newBox = Box(
                    point.subtract(useLo),
                    point.add(useHi)
            )


            // width, Y, depth

            if (ourSize != supposedSize) {
                println("Incorrect size! us: $ourSize, copy: $supposedSize")

                //*
                FocusRegionPacket(
                        newBox
                ).sendToClient(player)


                 //*/

            } else {

                doAction(player, EditAction(
                        player,
                        pasteBox,
                        face,
                        PasteOperation(cd, otherAxis = false),
                        listOf()
                ))

            }

            /*
            doAction(player, EditAction(
                    player,
                    pasteBox,
                    face,
                    PasteOperation(cd, otherAxis = false),
                    listOf()
            ))

             */

            /*
            FocusRegionPacket(
                    Box(
                            pasteBox.startBlock(),
                            pasteBox.startBlock().add(size)
                    )
            ).sendToClient(player)


             */





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