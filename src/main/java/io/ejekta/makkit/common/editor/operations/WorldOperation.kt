package io.ejekta.makkit.common.editor.operations

import io.ejekta.makkit.common.editor.data.EditAction
import kotlinx.serialization.Serializable
import net.minecraft.world.BlockView

@Serializable
sealed class WorldOperation {

    open fun getType(): OpType {
        return OpType.WALLS
    }

    open fun calculate(action: EditAction, view: BlockView) {}

    companion object {

        // Mod gets weird with negative numbers. I want the repeating behavior without the weirdness.
        fun modNoNegative(a: Int, b: Int): Int {
            return (a % b + b) % b
        }

    }

}



