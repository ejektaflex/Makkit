package ejektaflex.makkit.common.editor.data

import ejektaflex.makkit.common.ext.startBlock
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.world.World

data class CopyData(
        val data: Map<BlockPos, BlockState> = mutableMapOf(),
        val box: Box,
        val dir: Direction
)