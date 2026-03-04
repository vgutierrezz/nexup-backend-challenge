package main.kotlin.core.domain.exception

class InvalidQuantityException :
    DomainException("La cantidad debe ser mayor que 0")