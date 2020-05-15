package io.ejekta.makkit.common.editor

import io.ejekta.makkit.common.editor.data.EditAction
import io.ejekta.makkit.common.editor.data.UserEditProfile
import io.ejekta.makkit.common.enum.ClipboardMode
import io.ejekta.makkit.common.enum.UndoRedoMode
import io.ejekta.makkit.common.network.pakkits.client.ShadowBoxShowPacket
import io.ejekta.makkit.common.network.pakkits.server.ClipboardIntentPacket
import io.ejekta.makkit.common.network.pakkits.server.ShadowBoxUpdatePacket
import io.ejekta.makkit.common.network.pakkits.server.EditHistoryPacket
import io.ejekta.makkit.common.network.pakkits.server.EditWorldPacket
import net.minecraft.block.Blocks
import net.minecraft.item.BlockItem
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.LiteralText

object NetworkHandler {

    /*
        Contains edit data for individual users
     */
    private val userProfiles = mutableMapOf<String, UserEditProfile>()

    fun getProfileOf(player: ServerPlayerEntity): UserEditProfile {
        return userProfiles.getOrPut(player.uuidAsString) {
            UserEditProfile()
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
                intent.undoBox,
                intent.side,
                intent.op,
                // TODO will crash if item is not a BlockItem, change this eventually
                intent.palette.map {
                    if (it.item == Items.AIR) {
                        Blocks.AIR.defaultState
                    } else {
                        (it.item as BlockItem).block.defaultState
                    }
                }
        )

        try {
            getProfileOf(player).doAction(player, action)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun redirectRemoteBoxPreview(player: ServerPlayerEntity, pakkit: ShadowBoxUpdatePacket) {
        for (otherPlayer in player.world.players.filter { it != player }) {
            ShadowBoxShowPacket(pakkit).sendToClient(otherPlayer)
        }
    }

    fun handleCopyPaste(player: ServerPlayerEntity, pakkit: ClipboardIntentPacket) {
        val profile = getProfileOf(player)

        when (pakkit.mode) {
            ClipboardMode.COPY -> profile.copy(player, pakkit.box, pakkit.face)
            ClipboardMode.PASTE -> profile.paste(player, pakkit.box, pakkit.face)
            else -> throw Exception("Clipboard mode not implemented on server! ${pakkit.mode}")
        }
    }

    fun handleUndoRedo(player: ServerPlayerEntity, pakkit: EditHistoryPacket) {
        if (player.isCreative) {
            val profile = getProfileOf(player)

            val result = when (pakkit.mode) {
                UndoRedoMode.UNDO -> profile.undo(player)
                UndoRedoMode.REDO -> profile.redo(player)
                UndoRedoMode.CLEAR -> profile.clearHistory()
            }

            if (!result) {
                player.sendMessage(LiteralText("Could not ${pakkit.mode}!"), true)
            }
        } else {
            player.sendMessage(LiteralText("Must be in Creative Mode to use Makkit!"), true)
        }
    }

}