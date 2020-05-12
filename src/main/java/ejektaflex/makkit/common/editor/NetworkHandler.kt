package ejektaflex.makkit.common.editor

import ejektaflex.makkit.common.enum.UndoRedoMode
import ejektaflex.makkit.common.network.pakkits.client.ShadowBoxShowPacket
import ejektaflex.makkit.common.network.pakkits.server.ShadowBoxUpdatePacket
import ejektaflex.makkit.common.network.pakkits.server.EditHistoryPacket
import ejektaflex.makkit.common.network.pakkits.server.EditWorldPacket
import net.minecraft.block.Blocks
import net.minecraft.item.BlockItem
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.LiteralText
import net.minecraft.util.math.Box

object NetworkHandler {

    private val userHistories = mutableMapOf<String, UserActionHistory>()

    fun getHistoryOf(player: ServerPlayerEntity): UserActionHistory {
        val uuid = player.uuidAsString

        return userHistories.getOrPut(uuid) {
            UserActionHistory()
        }
    }

    fun handleEdit(player: ServerPlayerEntity, intent: EditWorldPacket) {

        if (!player.isCreative) {
            player.sendMessage(LiteralText("Must be in Creative Mode to use Makkit!"), true)
            return
        }

        val action = EditAction(
                player,
                intent.box,
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
            action.doInitialCommit(player)
            getHistoryOf(player).addToHistory(action)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun redirectRemoteBoxPreview(player: ServerPlayerEntity, pakkit: ShadowBoxUpdatePacket) {
        for (otherPlayer in player.world.players.filter { it != player }) {
            ShadowBoxShowPacket(pakkit).sendToClient(otherPlayer)
        }
    }

    fun handleUndoRedo(player: ServerPlayerEntity, pakkit: EditHistoryPacket) {
        if (player.isCreative) {
            val history = getHistoryOf(player)

            val result = when (pakkit.mode) {
                UndoRedoMode.UNDO -> history.undo(player)
                UndoRedoMode.REDO -> history.redo(player)
                UndoRedoMode.CLEAR -> history.clear()
            }

            if (!result) {
                player.sendMessage(LiteralText("Could not ${pakkit.mode}!"), true)
            }
        } else {
            player.sendMessage(LiteralText("Must be in Creative Mode to use Makkit!"), true)
        }
    }

}