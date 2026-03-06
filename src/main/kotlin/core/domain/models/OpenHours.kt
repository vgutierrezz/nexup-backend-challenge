package main.kotlin.core.domain.models

import main.kotlin.core.domain.exception.InvalidOpenTimeRangeException
import main.kotlin.core.domain.exception.InvalidOpenDaysException
import java.time.LocalTime
import java.time.DayOfWeek

class OpenHours (
    val daysOpen: Set<DayOfWeek>,
    val openTime: LocalTime,
    val closeTime: LocalTime
) {
    //Validaciones - Reglas de negocio
    init {
        if (daysOpen.isEmpty()) {
            throw InvalidOpenDaysException()
        }

        if (openTime >= closeTime) {
            throw InvalidOpenTimeRangeException()
        }
    }

    fun isOpen(day: DayOfWeek, time: LocalTime): Boolean {
        return day in daysOpen &&
                time >= openTime &&
                time < closeTime
    }

    fun getDescription(): String {
        val dayNames = mapOf(
            DayOfWeek.MONDAY to "Lun",
            DayOfWeek.TUESDAY to "Mar",
            DayOfWeek.WEDNESDAY to "Mié",
            DayOfWeek.THURSDAY to "Jue",
            DayOfWeek.FRIDAY to "Vie",
            DayOfWeek.SATURDAY to "Sáb",
            DayOfWeek.SUNDAY to "Dom"
        )

        val daysStr = daysOpen
            .sortedBy { it.value }
            .mapNotNull { dayNames[it] }
            .joinToString(", ")

        return "$daysStr: $openTime - $closeTime"
    }
}