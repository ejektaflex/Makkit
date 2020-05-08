package ejektaflex.makkit.common.world

import ejektaflex.makkit.common.ext.getBlockArray
import ejektaflex.makkit.common.ext.wallBlocks
import net.minecraft.util.math.Box
import kotlin.reflect.KClass

abstract class WorldOperation {
    abstract fun getType(): Type
    abstract fun execute(action: EditAction)

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

    override fun execute(action: EditAction) {
        for (pos in action.box.getBlockArray()) {
            action.edit(pos, action.palette.random())
        }
    }
}

class FillWallsOperation() : WorldOperation() {
    override fun getType() = Companion.Type.WALLS

    override fun execute(action: EditAction) {
        for (pos in action.box.wallBlocks()) {
            action.edit(pos, action.palette.random())
        }
    }
}

class RepeatOperation(val startBox: Box) : WorldOperation() {
    override fun getType() = Companion.Type.REPEAT

    override fun execute(action: EditAction) {
        TODO("Not yet implemented")
    }
}
