package io.ejekta.makkit.common.editor.operations

import io.ejekta.makkit.common.editor.data.EditAction
import io.ejekta.makkit.common.ext.getBlockArray
import io.ejekta.makkit.common.ext.getSize
import io.ejekta.makkit.common.ext.getStart
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView



class PatternOperation : WorldOperation() {
    override fun getType() = Companion.Type.PATTERN

    override fun calculate(action: EditAction, view: BlockView) {
        val startPos = BlockPos(action.undoBox.getStart())
        val afterBlocks = action.box.getBlockArray()

        println("Edit Action took in selection with ${action.box.getSize()}")

        println("Operating on this many blocks: ${action.undoBox.getBlockArray().size}, ${afterBlocks.size}")
        
        for (blockPos in afterBlocks) {
            val posRel = blockPos.subtract(startPos)
            val copySourcePos = startPos.add(BlockPos(
                    modNoNegative(posRel.x, action.undoBox.xLength.toInt()),
                    modNoNegative(posRel.y, action.undoBox.yLength.toInt()),
                    modNoNegative(posRel.z, action.undoBox.zLength.toInt())
            ))

            action.edit(blockPos, view.getBlockState(copySourcePos))
        }
    }
}