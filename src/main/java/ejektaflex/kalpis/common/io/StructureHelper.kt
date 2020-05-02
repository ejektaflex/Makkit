package ejektaflex.kalpis.common.io

import ejektaflex.kalpis.render.RenderBox
import net.minecraft.client.MinecraftClient
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.minecraft.util.InvalidIdentifierException
import net.minecraft.util.math.BlockPos

object StructureHelper {

    private val mc = MinecraftClient.getInstance()

    fun saveStructure(box: RenderBox, name: Identifier) {
        val structManager = (mc.world as ServerWorld).structureManager

        val structure = try {
            structManager.getStructureOrBlank(name)
        } catch (var8: InvalidIdentifierException) {
            throw Exception("Invalid Identifier for structure!")
        }

        structure.saveFromWorld(mc.world, BlockPos(box.pos), BlockPos(box.size), false, null)

    }

}