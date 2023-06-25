package io.ejekta.makkit.common.editor.operations

import io.ejekta.makkit.common.editor.data.EditAction
import io.ejekta.makkit.common.editor.operations.WorldOperation.Companion.modNoNegative
import io.ejekta.makkit.common.ext.getBlockArray
import io.ejekta.makkit.common.ext.getStart
import io.ejekta.makkit.common.ext.roundToVec3i
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.world.BlockView


@Serializable
class PatternOperation(val boxBefore: @Contextual Box, val afterBox: @Contextual Box) : WorldOperation() {
    override fun getType() = OpType.PATTERN

    override fun calculate(action: EditAction, view: BlockView) {
        val startPos = BlockPos(boxBefore.getStart().roundToVec3i())
        val afterBlocks = afterBox.getBlockArray()

        for (blockPos in afterBlocks) {
            val posRel = blockPos.subtract(startPos)
            val copySourcePos = startPos.add(BlockPos(
                    modNoNegative(posRel.x, boxBefore.xLength.toInt()),
                    modNoNegative(posRel.y, boxBefore.yLength.toInt()),
                    modNoNegative(posRel.z, boxBefore.zLength.toInt())
            ))

            action.edit(blockPos, view.getBlockState(copySourcePos))
        }
    }
}