package io.ejekta.makkit.client.mixin;

import io.ejekta.makkit.client.event.Events;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServersideDisconnect {

    @Shadow public ServerPlayerEntity player;

    @Environment(EnvType.CLIENT)
    @Inject(
            method = "onDisconnected",
            at = @At(value = "HEAD")
    )
    private void onDisconnected(Text reason, CallbackInfo ci) {

        Events.ServerDisconnectEvent.Companion.getDispatcher().invoker().invoke(
                new Events.ServerDisconnectEvent(player)
        );

    }

}
