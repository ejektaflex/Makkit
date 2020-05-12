package ejektaflex.makkit.common.editor

import ejektaflex.makkit.common.editor.operations.WorldOperation
import ejektaflex.makkit.common.enum.UndoRedoMode
import ejektaflex.makkit.common.ext.getEnd
import ejektaflex.makkit.common.ext.getStart
import ejektaflex.makkit.common.network.pakkits.client.FocusRegionPacket
import net.minecraft.block.BlockState
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.world.World

data class EditAction(
        val player: ServerPlayerEntity,
        val box: Box,
        val direction: Direction,
        val operation: WorldOperation,
        val palette: List<BlockState>
) {
    // Key: Position. Value: BeforeState, AfterState
    private var stateMap = mutableMapOf<BlockPos, Pair<BlockState, BlockState>>()

    fun doInitialCommit(player: ServerPlayerEntity) {
        calcChangeSet(player.world)
        optimize()
        commit(player.world)
    }

    fun edit(pos: BlockPos, state: BlockState) {
        if (pos !in stateMap) {
            stateMap[pos] = player.world.getBlockState(pos) to state
        } else {
            stateMap[pos] = stateMap[pos]!!.first to state
        }
    }

    fun clear(pos: BlockPos) {
        if (pos in stateMap) {
            stateMap.remove(pos)
        }
    }

    private fun optimize() {
        stateMap.filter {
            it.value.first == it.value.second
        }.map {
            it.key
        }.forEach { pos ->
            clear(pos)
        }
    }

    fun syncToWorldState(mode: UndoRedoMode) {
        for (entry in stateMap) {
            stateMap[entry.key] = when(mode) {
                UndoRedoMode.UNDO -> entry.value.first to player.world.getBlockState(entry.key)
                UndoRedoMode.REDO -> player.world.getBlockState(entry.key) to entry.value.second
                else -> throw Exception("You cannot sync edit history to world state with a CLEAR mode!")
            }
        }
    }

    private fun calcChangeSet(world: World) {
        operation.calculate(this, world)
    }

    fun select(player: ServerPlayerEntity) {
        FocusRegionPacket(box).sendToClient(player)
    }

    fun commit(world: World) {
        for (entry in stateMap) {
            world.setBlockState(entry.key, entry.value.second)
        }
    }

    fun revertCommit(world: World) {
        for (entry in stateMap) {
            world.setBlockState(entry.key, entry.value.first)
        }
    }

}