package ejektaflex.kalpis.common.world

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction

data class EditAction(
        val player: ServerPlayerEntity,
        val box: Box,
        val direction: Direction,
        val operation: WorldOperation,
        val palette: List<BlockState>
) {
    // Pos: BeforeState, AfterState
    private var stateMap = mutableMapOf<BlockPos, Pair<BlockState, BlockState>>()

    fun change(pos: BlockPos, state: BlockState) {
        if (pos !in stateMap) {
            stateMap[pos] = player.world.getBlockState(pos) to state
        } else {
            stateMap[pos] = stateMap[pos]!!.first to state
        }
    }

    fun revertChange(pos: BlockPos) {
        if (pos in stateMap) {
            stateMap.remove(pos)
        }
    }

    fun doApply() {
        operation.execute(this)
        for (entry in stateMap) {
            player.world.setBlockState(entry.key, entry.value.second)
        }
    }

    fun undoApply() {
        for (entry in stateMap) {
            player.world.setBlockState(entry.key, entry.value.first)
        }
    }

}