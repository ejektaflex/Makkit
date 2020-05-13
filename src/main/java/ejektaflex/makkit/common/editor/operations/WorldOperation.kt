package ejektaflex.makkit.common.editor.operations

import ejektaflex.makkit.common.editor.data.EditAction
import net.minecraft.world.BlockView
import kotlin.reflect.KClass

abstract class WorldOperation {

    abstract fun getType(): Type

    abstract fun calculate(action: EditAction, view: BlockView)

    companion object {
        enum class Type(val clazz: KClass<out WorldOperation>) {
            SET(FillBlocksOperation::class),
            WALLS(FillWallsOperation::class),
            REPEAT(RepeatOperation::class),
            PASTE(PasteOperation::class)
        }

        // Mod gets weird with negative numbers. I want the repeating behavior without the weirdness.
        fun modNoNegative(a: Int, b: Int): Int {
            return (a % b + b) % b
        }

    }

}



