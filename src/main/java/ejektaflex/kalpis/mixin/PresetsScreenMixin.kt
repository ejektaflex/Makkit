package ejektaflex.kalpis.mixin

import net.minecraft.block.Blocks
import net.minecraft.client.gui.screen.PresetsScreen
import net.minecraft.client.resource.language.I18n
import net.minecraft.item.ItemConvertible
import net.minecraft.world.biome.Biome
import net.minecraft.world.biome.Biomes
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import java.util.*

@Mixin(PresetsScreen::class)
object PresetsScreenMixin {
    @Shadow
    private fun addPreset(name: String, icon: ItemConvertible, biome: Biome, structures: List<String>, vararg layers: FlatChunkGeneratorLayer) {
    }

    init {
        addPreset(I18n.translate("betternether.flat_nether"),
                Blocks.NETHERRACK,
                Biomes.NETHER,
                Arrays.asList("decoration", "nether_city"),
                FlatChunkGeneratorLayer(63, Blocks.NETHERRACK),
                FlatChunkGeneratorLayer(1, Blocks.BEDROCK))
    }
}