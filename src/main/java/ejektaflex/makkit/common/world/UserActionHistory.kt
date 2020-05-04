package ejektaflex.makkit.common.world

import java.util.*

class UserActionHistory(val uuidString: String) {

    // TODO implement max history length
    private val maxHistLength = 5

    private var undoHistory = ArrayDeque<EditAction>()
    private var redoHistory = ArrayDeque<EditAction>()

    fun undo(): Boolean {
        return if (undoHistory.isEmpty()) {
            false
        } else {
            val result = undoHistory.pop()
            result.undoCommit()
            redoHistory.push(result)
            true
        }
    }

    fun redo(): Boolean {
        return if (redoHistory.isEmpty()) {
            false
        } else {
            val result = redoHistory.pop()
            result.commit()
            undoHistory.push(result)
            true
        }
    }

    fun addToHistory(action: EditAction) {
        undoHistory.push(action)
        redoHistory.clear()
    }

}