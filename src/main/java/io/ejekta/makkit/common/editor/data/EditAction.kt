package io.ejekta.makkit.common.editor.data

import io.ejekta.makkit.common.editor.operations.FillBlocksOperation
import io.ejekta.makkit.common.editor.operations.WorldOperation
import io.ejekta.makkit.common.enums.BlockMask
import io.ejekta.makkit.common.enums.UndoRedoMode
import io.ejekta.makkit.common.network.pakkits.client.FocusRegionPacket
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.world.World

data class EditAction(
        val player: ServerPlayerEntity,
        val box: Box,
        val undoBox: Box,
        val direction: Direction,
        val operation: WorldOperation = FillBlocksOperation(),
        val stacks: List<ItemStack> = listOf(),
        val options: EditWorldOptions = EditWorldOptions()
) {
    // Key: Position. Value: BeforeState, AfterState
    private var stateMap = mutableMapOf<BlockPos, Pair<BlockState, BlockState>>()

    val palette = BlockPalette(this)

    fun doInitialCommit(player: ServerPlayerEntity) {
        calcChangeSet(player.world)
        optimize()
        commit(player.world)
    }

    fun edit(pos: BlockPos, state: BlockState) {
        val block = player.world.getBlockState(pos).block
        when (options.blockMask) {
            BlockMask.ALL_BLOCKS -> doEdit(pos, state)
            BlockMask.NON_AIR -> if (block != Blocks.AIR) {
                doEdit(pos, state)
            }
            BlockMask.ONLY_AIR -> if (block == Blocks.AIR) {
                doEdit(pos, state)
            }
        }
    }

    private fun doEdit(pos: BlockPos, state: BlockState) {
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

    fun select(player: ServerPlayerEntity, mode: UndoRedoMode) {
        val boxToUse = when(mode) {
            UndoRedoMode.UNDO -> undoBox
            else -> box
        }
        FocusRegionPacket(boxToUse).sendToClient(player)
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