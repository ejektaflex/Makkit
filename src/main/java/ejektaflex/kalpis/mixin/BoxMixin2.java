package ejektaflex.kalpis.mixin;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

import javax.annotation.Nullable;

@Mixin(Box.class)
public abstract class BoxMixin2 {

    @Shadow
    private static Direction traceCollisionSide(
            Box box, Vec3d intersectingVector,
            double[] traceDistanceResult, Direction approachDirection,
            double xDelta, double yDelta, double zDelta
    ) throws Exception {
        throw new Exception("Unable to map!");
    }



}
