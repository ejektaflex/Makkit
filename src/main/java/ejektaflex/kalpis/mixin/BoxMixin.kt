package ejektaflex.kalpis.mixin

import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.gen.Invoker

@Mixin(Box::class)
object BoxMixin {


    @JvmStatic
    @Invoker
    private fun traceCollisionSide(
            box: Box, intersectingVector: Vec3d,
            traceDistanceResult: DoubleArray, approachDirection: Direction?,
            xDelta: Double, yDelta: Double, zDelta: Double
    ): Direction { return Direction.DOWN }

}