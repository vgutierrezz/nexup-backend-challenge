package main.kotlin.core.domain.exception

class InvalidOpenTimeRangeException :
    DomainException("La hora de apertura debe ser anterior a la de cierre")
