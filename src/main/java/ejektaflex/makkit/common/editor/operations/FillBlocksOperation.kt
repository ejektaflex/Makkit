package ejektaflex.makkit.common.editor.operations

import ejektaflex.makkit.common.editor.EditAction
import ejektaflex.makkit.common.ext.getBlockArray
import net.minecraft.world.BlockView

class FillBlocksOperation() : WorldOperation() {
    override fun getType() = Companion.Type.SET

    override fun calculate(action: EditAction, view: BlockView) {
        for (pos in action.box.getBlockArray()) {
            action.edit(pos, action.palette.random())
        }
    }
}