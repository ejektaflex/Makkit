package io.ejekta.makkit.common.editor.data

import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction

data class CopyData(
        val data: Map<BlockPos, BlockState> = mapOf(),
        val box: Box,
        val copySize: BlockPos,
        val dir: Direction
)