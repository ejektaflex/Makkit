package io.ejekta.makkit.client.compat

import io.ejekta.makkit.client.MakkitClient
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import java.util.*

object MixinUtil {
    fun getLocalPlayerById(id: UUID): PlayerEntity? {
        println(MakkitClient.remoteBoxMap.keys)
        return MinecraftClient.getInstance().world?.players?.firstOrNull { it.uuidAsString == id.toString() }
    }
}