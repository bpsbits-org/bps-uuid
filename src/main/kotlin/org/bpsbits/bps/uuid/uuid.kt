package org.bpsbits.bps.uuid

import java.time.ZonedDateTime
import java.util.UUID

/**
 * Extension functions for the standard [java.util.UUID] to make working with
 * UUID Version 7 easier and more convenient.
 *
 * These extensions are mainly for backward compatibility, Java interop,
 * and legacy code. For new code, prefer using the [UUIDv7] type directly.
 */

/**
 * Checks whether this UUID is a valid version 7 UUID.
 *
 * @return `true` if this is a valid UUIDv7, `false` otherwise.
 */
val UUID.isV7: Boolean get() = UUIDv7.isV7(this)

/**
 * Validates that this UUID is a version 7 UUID.
 *
 * @return The same [UUID] instance if it is valid.
 * @throws IllegalArgumentException if the UUID is not version 7.
 */
fun UUID.validateIsV7(): UUID {
    UUIDv7.validateUUID(this)
    return this
}

/**
 * Extracts the creation timestamp from a version 7 UUID.
 *
 * @return The date and time when this UUID was generated, or `null` if this is not a valid UUIDv7.
 */
fun UUID.resolveDate(): ZonedDateTime? = if (isV7) UUIDv7(this.toString()).date else null