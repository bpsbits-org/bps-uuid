package org.bpsbits.bps.test.uuid

import kotlinx.serialization.json.Json
import org.bpsbits.bps.uuid.UUIDv7
import org.bpsbits.bps.uuid.isV7
import org.bpsbits.bps.uuid.resolveDate
import org.bpsbits.bps.uuid.validateIsV7
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

class TestUUIDv7 {

    companion object {
        const val ITERATIONS = 250_000
    }

    @Test
    fun `UUIDV7 new should generate version 7 UUID`() {
        repeat(ITERATIONS) {
            val tmp = UUIDv7()
            assertTrue(UUIDv7.isV7(tmp.toUUID()), "Generated UUID is not a valid version 7 UUID: $tmp")
        }
    }

    @Test
    fun `UUIDV7 newString should generate string version of UUID v7`() {
        repeat(ITERATIONS) {
            val uuidString = UUIDv7.asString()
            assertTrue(UUIDv7.isStrV7(uuidString), "Generated string is not a valid version 7 UUID: $uuidString")
        }
    }

    @Test
    fun `UUIDv7 validateUUID should not throw exceptions for UUIDv7`() {
        repeat(ITERATIONS) {
            UUIDv7.validateUUID(UUIDv7().toUUID())
        }
    }

    @Test
    fun `UUIDv7 validateString should not throw exception for UUIDv7`() {
        repeat(ITERATIONS) {
            UUIDv7.validateString(UUIDv7().toString())
        }
    }

    @Test
    fun `UUID isv7 should generate valid v7 UUID`() {
        repeat(ITERATIONS) {
            val tmp = UUIDv7()
            assertTrue(tmp.toUUID().isV7, "Generated value is not valid version 7 UUID: $tmp")
        }
    }

    @Test
    fun `UUID validateIsUUIDv7 should not throw exceptions`() {
        repeat(ITERATIONS) {
            UUIDv7.asUUID().validateIsV7()
        }
    }

    @Test
    fun `UUIDv7 resolveDate should return ZonedDateTime from UUIDv7`() {
        repeat(ITERATIONS) {
            val tmp = UUIDv7().toUUID()
            val date = tmp.resolveDate()
            assertNotNull(date, "resolveDate() should never return null for a valid UUIDv7")
            assertTrue(date is ZonedDateTime, "resolveDate should return ZonedDateTime: $tmp")
        }
    }

    @Test
    fun `UUIDv7 date should return ZonedDateTime from UUIDv7`() {
        repeat(ITERATIONS) {
            UUIDv7().date
        }
    }

    @Test
    fun `UUIDv7 parseString should return UUIDv7`() {
        repeat(ITERATIONS) {
            val tmp = "   ${UUIDv7()}   "
            assertNotNull(UUIDv7.parseOrNull(tmp), "parseString() should return UUIDv7: $tmp")
        }
    }

    @Test
    fun `Serialization and deserialization should succeed`() {
        repeat(ITERATIONS) {
            val tmpVal = UUIDv7()
            val tmpJsonString = Json.encodeToString(tmpVal)
            val deserialized: UUIDv7 = Json.decodeFromString(tmpJsonString)
            assertEquals(tmpVal, deserialized)
        }
    }

    @Test
    fun `Serialized string should be valid UUIDv7 string`() {
        repeat(ITERATIONS) {
            val tmpVal = UUIDv7()
            val tmpStr = Json.encodeToString(tmpVal).removeSurrounding("\"")
            assertTrue(UUIDv7.isStrV7(tmpStr), "Encoding result should contain valid v7 UUID but contains: $tmpStr")
        }
    }

    @Test
    fun `UUIDv7 dateInfo should return proper data`() {
        repeat(ITERATIONS) {
            val tmp = UUIDv7()
            val dtInfo = tmp.dateInfo

            assertNotNull(dtInfo, "dateInfo should never return null")

            // Core guarantees from UUIDDateTimeInfo KDoc
            assertEquals("UTC", dtInfo.zone, "zone must always be UTC for UUIDv7")
            assertTrue(dtInfo.timestamp > 0, "timestamp must be a positive Unix millisecond value")

            // Consistency with the single source of truth (.date)
            val directDate = tmp.date
            assertEquals(
                directDate.toInstant().toEpochMilli(),
                dtInfo.timestamp,
                "dateInfo.timestamp must match the embedded timestamp from the UUID"
            )

            // All fields populated
            assertFalse(dtInfo.iso.isBlank(), "iso must contain a full ISO-8601 timestamp")
            assertFalse(dtInfo.date.isBlank(), "date must contain a YYYY-MM-DD string")
            assertFalse(dtInfo.time.isBlank(), "time must contain a time-of-day string")

            // Exact format validation (matches KDoc examples)
            assertTrue(dtInfo.iso.endsWith("Z"), "iso must end with Z (UTC)")
            assertTrue(
                dtInfo.date.matches(Regex("\\d{4}-\\d{2}-\\d{2}")),
                "date must be exactly YYYY-MM-DD"
            )
            assertTrue(
                dtInfo.time.matches(Regex("\\d{2}:\\d{2}:\\d{2}(\\.\\d{1,3})?")),
                "time must be HH:mm:ss(.SSS)"
            )

            // Practical sanity check
            val now = System.currentTimeMillis()
            assertTrue(
                dtInfo.timestamp in (now - 300_000)..(now + 30_000),
                "dateInfo.timestamp should be within ~5 minutes of now"
            )
        }
    }

}