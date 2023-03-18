package io.ejekta.makkit.common.editor.operations

import io.ejekta.makkit.common.editor.data.EditAction
import io.ejekta.makkit.common.editor.operations.serverside.PasteOperation
import kotlinx.serialization.Serializable
import net.minecraft.world.BlockView
import kotlin.reflect.KClass

@Serializable
abstract class WorldOperation {

    abstract fun getType(): OpType

    abstract fun calculate(action: EditAction, view: BlockView)

    companion object {

        // Mod gets weird with negative numbers. I want the repeating behavior without the weirdness.
        fun modNoNegative(a: Int, b: Int): Int {
            return (a % b + b) % b
        }

    }

}



