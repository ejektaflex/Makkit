package io.ejekta.makkit.common.editor.operations

import io.ejekta.makkit.common.editor.data.CopyData
import io.ejekta.makkit.common.editor.data.CopyHelper
import io.ejekta.makkit.common.editor.data.EditAction
import io.ejekta.makkit.common.ext.rotateClockwise
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.BlockView

class PasteOperation(val copy: CopyData) : WorldOperation() {

    override fun getType() = Companion.Type.PASTE

    override fun calculate(action: EditAction, view: BlockView) {

        fun Direction.rotatedClockwise(times: Int): Direction {
            return Direction.fromHorizontal((this.horizontal + times))
        }

        val dirs = listOf(
                Direction.WEST,
                Direction.NORTH,
                Direction.EAST,
                Direction.SOUTH
        )

        println("Copy dir: ${copy.dir}, action dir: ${action.direction}")

        for (entry in copy.data) {
            
            val timesToRotateClockwise = dirs.indexOf(action.direction) - 1

            // Handle block position rotation

            val rotPos = BlockPos(entry.key.rotateClockwise(timesToRotateClockwise + 1))

            val boxStartPos = CopyHelper.getLocalAxisStartPos(action.box, action.direction)

            // Handle block state rotation

            var state = entry.value

            if (state.contains(Properties.FACING)) {
                state = state.with(
                        Properties.FACING,
                        state.get(Properties.FACING)
                                .rotatedClockwise(timesToRotateClockwise))
            }

            if (state.contains(Properties.HORIZONTAL_FACING)) {
                state = state.with(
                        Properties.HORIZONTAL_FACING,
                        state.get(Properties.HORIZONTAL_FACING)
                                .rotatedClockwise(timesToRotateClockwise)
                )
            }

            action.edit(boxStartPos.add(rotPos), state)
        }

    }
}