package io.ejekta.makkit.common.editor.data

import io.ejekta.makkit.client.MakkitClient

class EditWorldOptions {
    // Whether items in a palette should be weighted based on their stack sizes
    var weightedPalette: Boolean = false
    // Whether rotatable blocks should be randomly rotated
    var randomRotate: Boolean = false
    // How the operation should interact with air blocks
    var airFillOption = MakkitClient.airModeOption
}