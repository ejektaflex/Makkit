package io.ejekta.makkit.common.editor.operations

import io.ejekta.makkit.common.editor.data.EditAction
import io.ejekta.makkit.common.ext.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.BlockView

@Serializable
class MirrorOperation(val flipCenter: @Contextual Vec3d) : WorldOperation() {

    override fun getType() = OpType.MIRROR

    override fun calculate(action: EditAction, view: BlockView) {

        for (block in action.box.getBlockArray()) {
            val vec = block.vec3d()
            val newPosUnconstrained = vec.subtract(vec.flipAround(flipCenter)).add(Vec3d(1.0, 1.0, 1.0))
            val newPos = vec.add(newPosUnconstrained.axisMask(action.direction).multiply(-1.0))
            action.edit(BlockPos(newPos), view.getBlockState(block).flippedOn(action.direction.axis))
        }

    }

}