package com.nexup.challenge.domain.models

import java.time.LocalTime
import java.time.DayOfWeek

data class OpenHours (
    val daysOpen: Set<DayOfWeek>,
    val openTime: LocalTime,
    val closeTime: LocalTime
)