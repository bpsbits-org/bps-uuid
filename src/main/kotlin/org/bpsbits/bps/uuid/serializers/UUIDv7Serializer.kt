package org.bpsbits.bps.uuid.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bpsbits.bps.uuid.UUIDv7

/**
 * Serializer for [UUIDv7].
 *
 * Converts [UUIDv7] to and from a plain UUID string in JSON.
 * This is the default serialization format used by the `UUIDv7` value class.
 */
object UUIDv7Serializer : KSerializer<UUIDv7> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("org.bpsbits.bps.uuid.UUIDv7", PrimitiveKind.STRING)

    /**
     * Serializes a [UUIDv7] as a plain UUID string.
     *
     * Example output: `"019d87a3-e6ae-7480-86dc-d6947d9dc112"`
     */
    override fun serialize(encoder: Encoder, value: UUIDv7) {
        encoder.encodeString(value.toString())
    }

    /**
     * Deserializes a UUID string into [UUIDv7].
     *
     * Uses [UUIDv7.valueOf] internally, so it validates that the string
     * is a valid version 7 UUID and throws [IllegalArgumentException] if it isn't.
     */
    override fun deserialize(decoder: Decoder): UUIDv7 {
        return UUIDv7.valueOf(decoder.decodeString())
    }
}