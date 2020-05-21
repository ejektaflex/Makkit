package io.ejekta.makkit.client.mixin;

import io.ejekta.makkit.client.MakkitClient;
import io.ejekta.makkit.client.event.Events;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
abstract class MouseMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "onMouseButton", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/options/KeyBinding;setKeyPressed(Lnet/minecraft/client/util/InputUtil$KeyCode;Z)V",
            ordinal = 0), cancellable = true)
    public void onMouseClicked(
            long window,
            int button,
            int action,
            int mods,
            CallbackInfo ci
    ) {

        if (MakkitClient.Companion.isInEditMode()) {
            Boolean cancelled = Events.MouseClickedEvent.Companion.getDispatcher().invoker().invoke(
                    new Events.MouseClickedEvent(button)
            );

            if (cancelled) {
                ci.cancel();
            }
        }

    }
}
