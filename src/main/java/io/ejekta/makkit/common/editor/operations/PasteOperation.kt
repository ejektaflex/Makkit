package io.ejekta.makkit.common.editor.operations

import io.ejekta.makkit.common.editor.data.CopyData
import io.ejekta.makkit.common.editor.data.EditAction
import io.ejekta.makkit.common.ext.rotateClockwise
import io.ejekta.makkit.common.ext.startBlock
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView

class PasteOperation(val copy: CopyData, otherAxis: Boolean) : WorldOperation() {

    override fun getType() = Companion.Type.PASTE

    override fun calculate(action: EditAction, view: BlockView) {

        for (entry in copy.data) {
            val newPos = action.box.startBlock().add(entry.key)

            action.edit(newPos, entry.value)
        }

    }
}