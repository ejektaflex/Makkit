package io.ejekta.makkit.common.editor.data

import io.ejekta.makkit.common.enums.BlockMask
import kotlinx.serialization.Serializable

@Serializable
data class EditWorldOptions(
    // Whether items in a palette should be weighted based on their stack sizes
    var weightedPalette: Boolean = false,
    // Whether rotatable blocks should be randomly rotated
    var randomRotate: Boolean = false,
    // How the operation should interact with air blocks
    var blockMask: BlockMask = BlockMask.ALL_BLOCKS
)