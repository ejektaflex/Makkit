package io.ejekta.makkit.common.editor.operations

import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

@Serializable
enum class OpType(val clazz: KClass<out WorldOperation>) {
    SET(FillBlocksOperation::class),
    WALLS(FillWallsOperation::class),
    PATTERN(PatternOperation::class),
    PASTE(PasteOperation::class),
    MIRROR(MirrorOperation::class)
}