package org.bpsbits.bps.uuid

import kotlinx.serialization.Serializable

/**
 * Human-readable date-time information extracted from a **UUID v7**.
 *
 * UUID version 7 embeds a 48-bit timestamp. This class provides a convenient,
 * ready-to-use representation of that timestamp in multiple formats.
 *
 * @property iso Full ISO-8601 timestamp with timezone.
 *              Example: `"2025-04-15T14:30:25.123456Z"`
 *
 * @property date Date portion in `YYYY-MM-DD` format.
 *               Example: `"2025-04-15"`
 *
 * @property time Time portion in `HH:mm:ss` (or with milliseconds if present).
 *               Example: `"14:30:25.123"`
 *
 * @property timestamp Unix timestamp in **milliseconds** since epoch (UTC).
 *                    Example: `1744722625123`
 *
 * @property zone Timezone identifier.
 *               For UUID v7 this is almost always `"UTC"`.
 *
 * @see [UUIDv7]
 */
@Serializable
data class UUIDDateTimeInfo(
    val iso: String,
    val date: String,
    val time: String,
    val timestamp: Long,
    val zone: String,
)
