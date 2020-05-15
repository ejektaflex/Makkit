package io.ejekta.makkit.common.editor.operations

import io.ejekta.makkit.common.editor.data.EditAction
import io.ejekta.makkit.common.editor.operations.serverside.PasteOperation
import net.minecraft.world.BlockView
import kotlin.reflect.KClass

abstract class WorldOperation {

    abstract fun getType(): Type

    abstract fun calculate(action: EditAction, view: BlockView)

    companion object {
        enum class Type(val clazz: KClass<out WorldOperation>) {
            SET(FillBlocksOperation::class),
            WALLS(FillWallsOperation::class),
            PATTERN(PatternOperation::class),
            PASTE(PasteOperation::class)
        }

        // Mod gets weird with negative numbers. I want the repeating behavior without the weirdness.
        fun modNoNegative(a: Int, b: Int): Int {
            return (a % b + b) % b
        }

    }

}



