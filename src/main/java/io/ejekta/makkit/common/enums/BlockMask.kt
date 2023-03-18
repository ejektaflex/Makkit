package io.ejekta.makkit.common.enums

import net.minecraft.text.Text

enum class BlockMask(val text: Text) {
    ALL_BLOCKS(Text.literal("Affect All Blocks")), // this shouldn't get displayed
    ONLY_AIR(Text.literal("Only Modify Air")),
    NON_AIR(Text.literal("Only Modify Non-Air")),
    OFFHAND(Text.literal("Only Modify Offhand Block"))
}