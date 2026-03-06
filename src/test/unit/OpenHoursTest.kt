package main.kotlin.core.domain.models

import main.kotlin.core.domain.exception.InvalidOpenDaysException
import main.kotlin.core.domain.exception.InvalidOpenTimeRangeException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.time.LocalTime
import java.time.DayOfWeek

class OpenHoursTest {

    @Test
    fun `crear horario con datos válidos`() {
        val openHours = OpenHours(
            daysOpen = setOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY),
            openTime = LocalTime.of(9, 0),
            closeTime = LocalTime.of(18, 0)
        )

        assertEquals(setOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY), openHours.daysOpen)
        assertEquals(LocalTime.of(9, 0), openHours.openTime)
        assertEquals(LocalTime.of(18, 0), openHours.closeTime)
    }

    @Test
    fun `crear horario con días vacíos lanza InvalidOpenDaysException`() {
        assertThrows(InvalidOpenDaysException::class.java) {
            OpenHours(
                daysOpen = emptySet(),
                openTime = LocalTime.of(9, 0),
                closeTime = LocalTime.of(18, 0)
            )
        }
    }

    @Test
    fun `crear horario donde openTime es igual a closeTime lanza InvalidOpenTimeRangeException`() {
        assertThrows(InvalidOpenTimeRangeException::class.java) {
            OpenHours(
                daysOpen = setOf(DayOfWeek.MONDAY),
                openTime = LocalTime.of(9, 0),
                closeTime = LocalTime.of(9, 0)
            )
        }
    }

    @Test
    fun `crear horario donde openTime es mayor a closeTime lanza InvalidOpenTimeRangeException`() {
        assertThrows(InvalidOpenTimeRangeException::class.java) {
            OpenHours(
                daysOpen = setOf(DayOfWeek.MONDAY),
                openTime = LocalTime.of(18, 0),
                closeTime = LocalTime.of(9, 0)
            )
        }
    }

    @Test
    fun `isOpen retorna true cuando el día y hora están dentro del horario`() {
        val openHours = OpenHours(
            daysOpen = setOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY),
            openTime = LocalTime.of(9, 0),
            closeTime = LocalTime.of(18, 0)
        )

        assertTrue(openHours.isOpen(DayOfWeek.MONDAY, LocalTime.of(12, 0)))
        assertTrue(openHours.isOpen(DayOfWeek.MONDAY, LocalTime.of(9, 0)))
        assertTrue(openHours.isOpen(DayOfWeek.TUESDAY, LocalTime.of(17, 59)))
    }

    @Test
    fun `isOpen retorna false cuando el día no está en daysOpen`() {
        val openHours = OpenHours(
            daysOpen = setOf(DayOfWeek.MONDAY),
            openTime = LocalTime.of(9, 0),
            closeTime = LocalTime.of(18, 0)
        )

        assertFalse(openHours.isOpen(DayOfWeek.TUESDAY, LocalTime.of(12, 0)))
        assertFalse(openHours.isOpen(DayOfWeek.SUNDAY, LocalTime.of(12, 0)))
    }

    @Test
    fun `isOpen retorna false cuando la hora es antes de openTime`() {
        val openHours = OpenHours(
            daysOpen = setOf(DayOfWeek.MONDAY),
            openTime = LocalTime.of(9, 0),
            closeTime = LocalTime.of(18, 0)
        )

        assertFalse(openHours.isOpen(DayOfWeek.MONDAY, LocalTime.of(8, 59)))
    }

    @Test
    fun `isOpen retorna false cuando la hora es igual o después de closeTime`() {
        val openHours = OpenHours(
            daysOpen = setOf(DayOfWeek.MONDAY),
            openTime = LocalTime.of(9, 0),
            closeTime = LocalTime.of(18, 0)
        )

        assertFalse(openHours.isOpen(DayOfWeek.MONDAY, LocalTime.of(18, 0)))
        assertFalse(openHours.isOpen(DayOfWeek.MONDAY, LocalTime.of(18, 1)))
    }

    @Test
    fun `crear horario válido con todos los días de la semana`() {
        val openHours = OpenHours(
            daysOpen = setOf(
                DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY
            ),
            openTime = LocalTime.of(7, 0),
            closeTime = LocalTime.of(23, 0)
        )

        assertEquals(7, openHours.daysOpen.size)
    }
}

