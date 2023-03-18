package io.ejekta.makkit.common.editor.operations.serverside

import io.ejekta.makkit.common.editor.data.CopyData
import io.ejekta.makkit.common.editor.data.CopyHelper
import io.ejekta.makkit.common.editor.data.EditAction
import io.ejekta.makkit.common.editor.operations.OpType
import io.ejekta.makkit.common.editor.operations.WorldOperation
import io.ejekta.makkit.common.ext.rotateClockwise
import io.ejekta.makkit.common.ext.rotated
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView

class PasteOperation(val copy: CopyData) : WorldOperation() {

    override fun getType() = OpType.PASTE

    override fun calculate(action: EditAction, view: BlockView) {

        for (entry in copy.data) {

            val timesToRotateClockwise = (action.direction.horizontal + 3) % 4

            var state = entry.value

            // Handle block position rotation
            val rotPos = BlockPos(entry.key.rotateClockwise(timesToRotateClockwise))
            val boxStartPos = CopyHelper.getLocalAxisStartPos(action.box, action.direction)

            // Handle block state rotation

            // Amount of rotation to blockstates is modified by copy direction
            // since we never put blockstate rotation into a common format
            val stateRotate = modNoNegative(action.direction.horizontal - copy.dir.horizontal, 4)

            state = state.rotated(stateRotate)

            action.edit(boxStartPos.add(rotPos), state)
        }

    }
}