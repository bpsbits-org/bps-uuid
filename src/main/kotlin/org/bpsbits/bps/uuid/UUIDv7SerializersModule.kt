package org.bpsbits.bps.uuid

import kotlinx.serialization.modules.SerializersModule
import org.bpsbits.bps.uuid.serializers.UUIDv7Serializer

/**
 * A pre-configured [SerializersModule] that registers [UUIDv7Serializer]
 * for the [UUIDv7] type.
 *
 * ### Usage
 *
 * This module is **optional** and only needed in advanced scenarios where
 * you build your own `Json`, `CBOR`, or other format instance manually.
 *
 * Most users (including Quarkus with `quarkus-rest-kotlin-serialization`)
 * do **not** need this module — the `@Serializable(with = UUIDv7Serializer::class)`
 * annotation on [UUIDv7] already provides automatic serialization support.
 *
 * ### Example
 *
 * ```kotlin
 * import kotlinx.serialization.json.Json
 * import org.bpsbits.bps.uuid.UUIDv7SerializersModule
 *
 * val json = Json {
 *     serializersModule = UUIDv7SerializersModule
 *
 *     // You can also merge it with your existing module:
 *     // serializersModule = UUIDv7SerializersModule + myOtherModule
 * }
 *
 * @Serializable
 * data class MyDto(val id: UUIDv7, val name: String)
 * ```
 *
 * @see UUIDv7
 * @see UUIDv7Serializer
 */
@Suppress("unused")
val UUIDv7SerializersModule: SerializersModule = SerializersModule {
    contextual(UUIDv7::class, UUIDv7Serializer)
}