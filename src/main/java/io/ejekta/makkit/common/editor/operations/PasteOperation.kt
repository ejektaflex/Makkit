package io.ejekta.makkit.common.editor.operations

import io.ejekta.makkit.common.editor.data.CopyData
import io.ejekta.makkit.common.editor.data.EditAction
import io.ejekta.makkit.common.ext.rotateClockwise
import io.ejekta.makkit.common.ext.startBlock
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.BlockView

class PasteOperation(val copy: CopyData, otherAxis: Boolean) : WorldOperation() {

    override fun getType() = Companion.Type.PASTE

    override fun calculate(action: EditAction, view: BlockView) {


        val dirs = listOf(
                Direction.NORTH,
                Direction.EAST,
                Direction.SOUTH,
                Direction.WEST
        )



        for (entry in copy.data) {

            var rotPos = entry.key

            for (i in 0 until dirs.indexOf(action.direction) - dirs.indexOf(copy.dir)) {
                rotPos = BlockPos(rotPos.rotateClockwise(1))
            }

            rotPos = BlockPos(rotPos.rotateClockwise(1))

            action.edit(action.box.startBlock().add(rotPos), entry.value)
        }

    }
}