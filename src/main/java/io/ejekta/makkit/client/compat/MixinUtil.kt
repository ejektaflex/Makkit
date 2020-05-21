package io.ejekta.makkit.client.compat

import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import java.util.*

object MixinUtil {
    fun getLocalPlayerById(id: UUID): PlayerEntity? {
        return MinecraftClient.getInstance().world?.players?.firstOrNull { it.uuidAsString == id.toString() }
    }
}