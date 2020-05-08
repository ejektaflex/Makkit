package ejektaflex.makkit.common.world

import ejektaflex.makkit.common.ext.getBlockArray
import ejektaflex.makkit.common.ext.getEnd
import ejektaflex.makkit.common.ext.getStart
import ejektaflex.makkit.common.ext.wallBlocks
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.world.World
import kotlin.reflect.KClass

abstract class WorldOperation {
    abstract fun getType(): Type
    abstract fun calculate(action: EditAction, world: World)

    companion object {
        enum class Type(val clazz: KClass<out WorldOperation>) {
            SET(FillBlocksOperation::class),
            WALLS(FillWallsOperation::class),
            REPEAT(RepeatOperation::class)
        }
    }
}

class FillBlocksOperation() : WorldOperation() {
    override fun getType() = Companion.Type.SET

    override fun calculate(action: EditAction, world: World) {
        for (pos in action.box.getBlockArray()) {
            action.edit(pos, action.palette.random())
        }
    }
}

class FillWallsOperation() : WorldOperation() {
    override fun getType() = Companion.Type.WALLS

    override fun calculate(action: EditAction, world: World) {
        for (pos in action.box.wallBlocks()) {
            action.edit(pos, action.palette.random())
        }
    }
}

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
