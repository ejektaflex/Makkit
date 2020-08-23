package io.ejekta.makkit.client.mixin;

import io.ejekta.makkit.client.event.Events;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PlayerInventory.class)
public abstract class InventoryScrolledMixin {


    @Shadow public int selectedSlot;

    @Environment(EnvType.CLIENT)
    @Inject(method = "scrollInHotbar", at = @At(value = "TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    void onMouseScroll(double scrollAmount, CallbackInfo ci) {
        // post event
        Events.InventoryScrolledEvent.Companion.getDispatcher().invoker().invoke(
                new Events.InventoryScrolledEvent(scrollAmount, selectedSlot)
        );
    }

}
