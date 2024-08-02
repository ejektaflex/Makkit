package io.ejekta.makkit.client.editor.drag.tools

import com.mojang.serialization.Lifecycle
import io.ejekta.kambrik.registration.KambrikAutoRegistrar
import io.ejekta.makkit.client.editor.drag.DragTool
import io.ejekta.makkit.client.editor.handle.Handle
import io.ejekta.makkit.common.MakkitCommon
import kotlinx.serialization.Serializable
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider.FabricTagBuilder
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.SimpleRegistry
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier

//typealias ToolCreator = @Serializable (handle: Handle) -> DragTool
//
//object MakkitTools : KambrikAutoRegistrar {
//    override fun getId() = "makkit"
//
//
//    val ToolRegistryKey: RegistryKey<Registry<ToolCreator>> = RegistryKey
//        .ofRegistry(Identifier(MakkitCommon.ID, "tools"))
//
//    val ToolRegistry = SimpleRegistry(ToolRegistryKey, Lifecycle.stable())
//
//    val TAG_MOVE_TOOLS = TagKey.of(ToolRegistryKey, Identifier(MakkitCommon.ID, "move_tools"))
//
//    val TOOL_MOVE_AXIAL = "move_axial".forRegistration(ToolRegistry, ::MoveToolAxial)
//
//    override fun afterRegistration() {
//        val mtb = FabricTagBuilder()
//    }
//
//}