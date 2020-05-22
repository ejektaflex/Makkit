package io.ejekta.makkit.common.enums

import net.minecraft.text.LiteralText
import net.minecraft.text.Text

enum class AirFillOption(val text: Text) {
    ALL_BLOCKS(LiteralText("Affect All Blocks")), // this shouldn't get displayed
    ONLY_AIR(LiteralText("Only Modify Air")),
    EXCLUDE_AIR(LiteralText("Only Modify Non-Air"))
}