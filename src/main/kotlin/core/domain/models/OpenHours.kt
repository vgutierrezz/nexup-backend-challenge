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
}