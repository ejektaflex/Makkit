package ejektaflex.makkit.common.editor

import ejektaflex.makkit.common.enum.UndoRedoMode
import net.minecraft.server.network.ServerPlayerEntity
import java.util.*

class UserActionHistory {

    private var undoHistory = ArrayDeque<EditAction>()
    private var redoHistory = ArrayDeque<EditAction>()

    fun clear(): Boolean {
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

    companion object {
        private const val maxHistLength = 50
    }

}