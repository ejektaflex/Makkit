package ejektaflex.makkit.common.world

import ejektaflex.makkit.common.enum.UndoRedoMode
import ejektaflex.makkit.common.network.pakkits.EditHistoryPacket
import ejektaflex.makkit.common.network.pakkits.EditWorldPacket
import net.minecraft.block.Blocks
import net.minecraft.item.BlockItem
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.LiteralText
import net.minecraft.util.math.Box

object WorldEditor {

    private val userHistories = mutableMapOf<String, UserActionHistory>()

    fun getHistoryOf(player: ServerPlayerEntity): UserActionHistory {
        val uuid = player.uuidAsString

        return userHistories.getOrPut(uuid) {
            UserActionHistory()
        }
    }

    fun handleEdit(player: ServerPlayerEntity, intent: EditWorldPacket) {
        val action = EditAction(
                player,
                Box(intent.start, intent.end),
                intent.side,
                intent.op,
                // will crash if item is not a BlockItem, change this eventually
                intent.palette.map {
                    if (it.item == Items.AIR) {
                        Blocks.AIR.defaultState
                    } else {
                        (it.item as BlockItem).block.defaultState
                    }
                }
        )

        try {
            action.calcChangeSet()
            action.commit()

            getHistoryOf(player).addToHistory(action)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun handleUndoRedo(player: ServerPlayerEntity, pakkit: EditHistoryPacket) {

        println("Handling ${pakkit.mode} of ${player.displayName}")

        val history = getHistoryOf(player)

        val result = when (pakkit.mode) {
            UndoRedoMode.UNDO -> history.undo()
            UndoRedoMode.REDO -> history.redo()
            UndoRedoMode.CLEAR -> history.clear()
        }

        if (!result) {
            player.sendMessage(LiteralText("Could not ${pakkit.mode}!"), true)
        }

    }


}