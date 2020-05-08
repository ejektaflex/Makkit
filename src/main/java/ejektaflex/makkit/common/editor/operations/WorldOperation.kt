package ejektaflex.makkit.common.editor.operations

import ejektaflex.makkit.common.editor.EditAction
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



