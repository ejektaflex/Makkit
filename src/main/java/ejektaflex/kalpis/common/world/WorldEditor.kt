package ejektaflex.kalpis.common.world

import ejektaflex.kalpis.common.io.EditIntent
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import java.util.*

object WorldEditor {

    private val stackMap = mutableMapOf<String, Stack<EditAction>>()

    //fun handleNetworkOperation(player: ServerPlayerEntity, start: BlockPos, end: BlockPos, side: Int, op: Int) {


    fun handleNetworkOperation(player: ServerPlayerEntity, intent: EditIntent) {
        val action = EditAction(
                player,
                Box(intent.start, intent.end),
                intent.side,
                intent.op
        )

        try {
            action.operation.execute(action)
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