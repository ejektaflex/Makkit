package io.ejekta.makkit.common.editor.operations

import io.ejekta.makkit.common.editor.data.EditAction
import io.ejekta.makkit.common.ext.getBlockArray
import io.ejekta.makkit.common.ext.getStart
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.world.BlockView



class RepeatOperation(val boxBefore: Box) : WorldOperation() {
    override fun getType() = Companion.Type.REPEAT

    override fun calculate(action: EditAction, view: BlockView) {
        val startPos = BlockPos(boxBefore.getStart())
        val afterBlocks = action.box.getBlockArray()
        
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