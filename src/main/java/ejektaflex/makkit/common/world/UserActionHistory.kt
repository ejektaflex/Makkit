package ejektaflex.makkit.common.world

import java.util.*

class UserActionHistory {

    private var undoHistory = ArrayDeque<EditAction>()
    private var redoHistory = ArrayDeque<EditAction>()

    fun undo(): Boolean {
        return if (undoHistory.isEmpty()) {
            false
        } else {
            undoHistory.pop().let {
                it.revertCommit()
                redoHistory.push(it)
            }
            true
        }
    }

    fun redo(): Boolean {
        return if (redoHistory.isEmpty()) {
            false
        } else {
            redoHistory.pop().let {
                it.commit()
                undoHistory.push(it)
            }
            true
        }
    }

    fun addToHistory(action: EditAction) {
        undoHistory.push(action)
        if (undoHistory.size > maxHistLength) {
            undoHistory.removeLast()
        }
        redoHistory.clear()
    }

    companion object {
        private const val maxHistLength = 5
    }

}