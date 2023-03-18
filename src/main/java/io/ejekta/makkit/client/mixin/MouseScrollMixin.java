package io.ejekta.makkit.client.mixin;

import io.ejekta.makkit.client.event.Events;
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
        double f = (MinecraftClient.getInstance().options.getDiscreteMouseScroll().getValue() ? Math.signum(e) : e);
        // post event
        Events.MouseScrollEvent.Companion.getDispatcher().invoker().invoke(
                new Events.MouseScrollEvent(f)
        );
    }

}
