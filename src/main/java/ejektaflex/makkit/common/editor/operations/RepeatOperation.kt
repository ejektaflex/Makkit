package ejektaflex.makkit.common.editor.operations

import ejektaflex.makkit.common.editor.EditAction
import ejektaflex.makkit.common.ext.getBlockArray
import ejektaflex.makkit.common.ext.getStart
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.world.World

class RepeatOperation(val boxBefore: Box) : WorldOperation() {
    override fun getType() = Companion.Type.REPEAT

    override fun calculate(action: EditAction, world: World) {
        val boxAfter = action.box
        val afterBlocks = action.box.getBlockArray()

        val isPositive = !(
                boxAfter.getStart().x < boxBefore.getStart().x
                        || boxAfter.getStart().y < boxBefore.getStart().y
                        || boxAfter.getStart().z < boxBefore.getStart().z
                )

        if (isPositive) {
            val startPos = BlockPos(boxBefore.getStart())
            for (blockPos in afterBlocks) {
                val posRel = blockPos.subtract(startPos)

                val copySourcePos = startPos.add(BlockPos(
                        posRel.x % boxBefore.xLength,
                        posRel.y % boxBefore.yLength,
                        posRel.z % boxBefore.zLength
                ))

                action.edit(blockPos, world.getBlockState(copySourcePos))
            }
        } else {
//            val startPos = BlockPos(boxBefore.getEnd())
        }
    }
}