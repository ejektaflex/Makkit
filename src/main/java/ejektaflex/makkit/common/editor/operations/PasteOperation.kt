package ejektaflex.makkit.common.editor.operations

import ejektaflex.makkit.common.editor.data.CopyData
import ejektaflex.makkit.common.editor.data.EditAction
import ejektaflex.makkit.common.ext.startBlock
import net.minecraft.world.BlockView

class PasteOperation(val copy: CopyData) : WorldOperation() {

    override fun getType() = Companion.Type.PASTE

    override fun calculate(action: EditAction, view: BlockView) {

        for (entry in copy.data) {


            val newPos = action.box.startBlock().add(entry.key)

            action.edit(newPos, entry.value)
        }

    }
}