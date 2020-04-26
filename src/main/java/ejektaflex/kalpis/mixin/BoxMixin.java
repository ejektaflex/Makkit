package ejektaflex.kalpis.mixin;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Box.class)
public interface BoxMixin {
    @Invoker("traceCollisionSide")
    static Direction traceCollisionSide(Box box, Vec3d intersectingVector, double[] traceDistanceResult, Direction approachDirection, double xDelta, double yDelta, double zDelta) {
        throw new UnsupportedOperationException("Mixin wasn't applied! This shouldn't happen!");
    }
}
