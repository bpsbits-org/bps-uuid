package org.bpsbits.bps.uuid

import kotlinx.serialization.Serializable
import org.bpsbits.bps.uuid.serializers.UUIDv7Serializer
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.UUID
import java.util.concurrent.ThreadLocalRandom
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * A type-safe UUID Version 7 (time-ordered).
 *
 * Create it only with `UUIDv7()` — no other constructor is available.
 * It behaves like a regular UUID but guarantees version 7 and carries a timestamp.
 *
 * Thread-safe for creation, parsing, validation, and date extraction.
 */
@ConsistentCopyVisibility
@Serializable(with = UUIDv7Serializer::class)
@Schema(
    type = SchemaType.STRING, format = "uuid",
    description = "A time-ordered UUID Version 7 (RFC 9562)",
    example = "019d87a3-e6ae-7480-86dc-d6947d9dc112"
)
data class UUIDv7 internal constructor(private val raw: String) {

    companion object {

        /**
         * Creates a new UUIDv7.
         *
         * This is the **only** intended way to get a fresh UUIDv7.
         */
        @JvmStatic
        operator fun invoke(): UUIDv7 = UUIDv7(asString())

        /**
         * Generates a version 7 UUID based on the current timestamp and random values.
         *
         * This UUID uses the current time for its most significant bits, combined with
         * some extra precision for sub-millisecond accuracy. The least significant bits
         * are randomly generated to ensure uniqueness.
         *
         * @return A newly generated [java.util.UUID] version 7.
         */
        @JvmStatic
        private fun randomV7UUID(): UUID {
            val cInst = Instant.now()
            val epochMilliSeconds = cInst.toEpochMilli()
            val nanoAdjustmentWithinMilli = cInst.nano % 1_000_000
            val subMillisecondPrecision = (nanoAdjustmentWithinMilli / 100).toLong()
            var highPrecisionTimestamp = (epochMilliSeconds and 0xFFFFFFFFFFFFL shl 16) or (subMillisecondPrecision and 0xFFF shl 4)
            highPrecisionTimestamp = (highPrecisionTimestamp and -0xF001L) or (7L shl 12)
            var random64BitValue = ThreadLocalRandom.current().nextLong()
            random64BitValue = (random64BitValue and 0x3FFFFFFFFFFFFFFFL) or (0x2L shl 62)
            return UUID(highPrecisionTimestamp, random64BitValue)
        }

        /** Internal helper: extracts the timestamp part from a UUIDv7. */
        private fun extractTimestamp(uuid: UUID): Long = (uuid.mostSignificantBits shr 16) and 0xFFFFFFFFFFFFL

        /** Internal helper: checks if the extracted timestamp can be turned into a valid ZonedDateTime. */
        private fun isValidTimestamp(timestamp: Long): Boolean = try {
            if (timestamp < 0) return false
            Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.UTC)
            true
        } catch (_: IllegalArgumentException) {
            false
        }

        /**
         * Checks if the given UUID is version 7.
         *
         * @return True if the UUID is version 7, false otherwise.
         */
        @JvmStatic
        fun isV7(uuidToCheck: UUID): Boolean {
            if (uuidToCheck.version() != 7) return false
            return isValidTimestamp(extractTimestamp(uuidToCheck))
        }

        /**
         * Checks if the given string represents a valid UUIDv7.
         *
         * @return True if the string is a valid version 7 UUID, false otherwise.
         */
        @JvmStatic
        fun isStrV7(str: String): Boolean {
            return try {
                val uuid = UUID.fromString(str.trim())
                isV7(uuid)
            } catch (_: IllegalArgumentException) {
                false
            }
        }

        /**
         * Parses a string into UUIDv7 or returns null if invalid.
         *
         * @return [UUIDv7] instance or null if the string is not a valid v7 UUID.
         */
        @JvmStatic
        fun parseOrNull(str: String): UUIDv7? = try {
            valueOf(str)
        } catch (_: IllegalArgumentException) {
            null
        }

        /**
         * Returns a new [UUIDv7] as a string.
         */
        fun asString(): String = randomV7UUID().toString()

        /**
         * Returns a new raw [java.util.UUID] version 7.
         */
        fun asUUID(): UUID = randomV7UUID()

        /**
         * Parses a string into [UUIDv7].
         *
         * @return An [UUIDv7] object.
         * @throws [IllegalArgumentException] if the string is not a valid UUID or not version 7.
         */
        @JvmStatic
        fun valueOf(str: String): UUIDv7 {
            val trimmed = str.trim()
            val uuid = try {
                UUID.fromString(trimmed)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Given string does not contain a UUID: '$str'", e)
            }
            if (isV7(uuid)) return UUIDv7(trimmed) else {
                throw IllegalArgumentException("Given string does not contain version 7 of UUID: $str")
            }
        }

        /**
         * Ensures the provided identifier is a version 7 UUID.
         *
         * @param uuidToValidate The identifier to check.
         * @return The [UUIDv7] if it passes the validation.
         * @throws [IllegalArgumentException] if the UUID is not version 7.
         */
        @JvmStatic
        fun validateUUID(uuidToValidate: UUID): UUIDv7 {
            require(isV7(uuidToValidate)) { "Not v7 UUID: $uuidToValidate. Should contain datetime and version number." }
            return UUIDv7(uuidToValidate.toString())
        }

        /**
         * Ensures that the provided string is a representation of version 7 UUID.
         *
         * @param stringToValidate The identifier to check
         * @return The trimmed string it passes the validation.
         * @throws IllegalArgumentException if the string isn't UUIDv7
         */
        @JvmStatic
        fun validateString(stringToValidate: String): String = valueOf(stringToValidate.trim()).toString()

    }

    /**
     * Returns a string representation of current [UUIDv7].
     */
    override fun toString(): String = raw

    /**
     * Returns the underlying raw [java.util.UUID].
     */
    fun toUUID(): UUID = UUID.fromString(raw)

    /**
     * The creation date and time of this UUIDv7 (extracted from its timestamp).
     */
    val date: ZonedDateTime
        get() {
            val timestamp = extractTimestamp(this.toUUID())
            return Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.UTC)
        }

    /**
     * The creation date and time of this UUIDv7 in a structured form.
     */
    val dateInfo: UUIDDateTimeInfo
        get() {
            val dt = date
            return UUIDDateTimeInfo(
                iso = dt.format(DateTimeFormatter.ISO_INSTANT),
                date = dt.format(DateTimeFormatter.ISO_LOCAL_DATE),
                time = dt.format(DateTimeFormatter.ISO_LOCAL_TIME),
                timestamp = dt.toInstant().toEpochMilli(),
                zone = if (dt.zone == ZoneOffset.UTC) "UTC" else dt.zone.id
            )
        }

}