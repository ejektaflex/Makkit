package io.ejekta.makkit.common.editor.operations

import io.ejekta.makkit.common.editor.data.CopyData
import io.ejekta.makkit.common.editor.data.CopyHelper
import io.ejekta.makkit.common.editor.data.EditAction
import io.ejekta.makkit.common.ext.rotateClockwise
import io.ejekta.makkit.common.ext.startBlock
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3i
import net.minecraft.world.BlockView

class PasteOperation(val copy: CopyData, otherAxis: Boolean) : WorldOperation() {

    override fun getType() = Companion.Type.PASTE

    override fun calculate(action: EditAction, view: BlockView) {


        val dirs = listOf(
                Direction.WEST,
                Direction.NORTH,
                Direction.EAST,
                Direction.SOUTH
        )

        println("Copy dir: ${copy.dir}, action dir: ${action.direction}")
        
        for (entry in copy.data) {

            var rotPos = entry.key

            var numRots = 0

            // offset by difference between picked face and copy face
            var pickDir = action.direction
            while (pickDir != copy.dir) {
                pickDir = pickDir.rotateYCounterclockwise()
                numRots++
            }

            // offset by current direction also
            numRots += dirs.indexOf(pickDir)

            for (i in 0 until numRots) {
                rotPos = BlockPos(rotPos.rotateClockwise(Vec3i.ZERO))
            }


            val start = CopyHelper.getCopyBoxPos(action.box, action.direction)

            action.edit(start.add(rotPos), entry.value)
        }

    }
}