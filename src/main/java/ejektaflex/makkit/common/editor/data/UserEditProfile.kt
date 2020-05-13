package ejektaflex.makkit.common.editor.data

import ejektaflex.makkit.common.editor.operations.PasteOperation
import ejektaflex.makkit.common.enum.UndoRedoMode
import ejektaflex.makkit.common.ext.getBlockArray
import ejektaflex.makkit.common.ext.getSize
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.LiteralText
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import java.util.*

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

    fun copy(player: ServerPlayerEntity, box: Box, face: Direction) {
        val stateMap = box.getBlockArray().map { it to player.world.getBlockState(it) }.toMap()
        copyData = CopyData(stateMap, box, face)
        player.sendMessage(LiteralText("Copied data to clipboard!"), true)
    }

    fun paste(player: ServerPlayerEntity, pasteBox: Box, face: Direction) {

        if (copyData != null) {
            val data = copyData!!

            if (pasteBox.getSize() == data.box.getSize()) {
                doAction(player, EditAction(
                        player,
                        pasteBox,
                        face,
                        PasteOperation(data),
                        listOf()
                ))
            } else {
                player.sendMessage(LiteralText("wrong size"), true)
            }

        } else {
            player.sendMessage(LiteralText("Can't paste, you haven't copied anything"), true)
        }



    }


    companion object {
        private const val maxHistLength = 50
    }

}