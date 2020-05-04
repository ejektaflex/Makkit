package ejektaflex.makkit.common.world

import ejektaflex.makkit.client.network.EditIntentPacket
import net.minecraft.block.Blocks
import net.minecraft.item.BlockItem
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.Box
import java.util.*

object WorldEditor {

    private val stackMap = mutableMapOf<String, Stack<EditAction>>()

    fun handleNetworkOperation(player: ServerPlayerEntity, intent: EditIntentPacket) {
        println("Handling network operation")
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
            action.doApply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun handleUndo(player: ServerPlayerEntity) {
        if (player.uuidAsString !in stackMap) {
            return
        } else {

        }
    }









}