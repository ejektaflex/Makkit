package io.ejekta.makkit.common.editor.operations

import io.ejekta.makkit.common.editor.data.EditAction
import io.ejekta.makkit.common.ext.getBlockArray
import net.minecraft.world.BlockView

class FillBlocksOperation() : WorldOperation() {
    override fun getType() = Companion.Type.SET

    override fun calculate(action: EditAction, view: BlockView) {
        for (pos in action.selectionBox.getBlockArray()) {
            action.edit(pos, action.palette.random())
        }
    }
}