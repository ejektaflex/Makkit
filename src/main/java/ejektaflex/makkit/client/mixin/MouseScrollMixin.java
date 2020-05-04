package ejektaflex.makkit.client.mixin;

import ejektaflex.makkit.client.event.Events;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public abstract class MouseScrollMixin {

    @Environment(EnvType.CLIENT)
    @Inject(method = "onMouseScroll", at = @At(value = "HEAD"))
    private void onMouseScroll(long window, double d, double e, CallbackInfo ci) {
        double f = (MinecraftClient.getInstance().options.discreteMouseScroll ? Math.signum(e) : e);
        Events.MouseScrollEvent.Companion.getDispatcher().invoker().invoke(
                new Events.MouseScrollEvent(f)
        );
    }

}
