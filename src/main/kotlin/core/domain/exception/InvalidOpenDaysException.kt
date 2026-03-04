package main.kotlin.core.domain.exception

class InvalidOpenDaysException :
    DomainException("Debe especificarse al menos un día de apertura")