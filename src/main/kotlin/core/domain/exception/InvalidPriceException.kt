package main.kotlin.core.domain.exception

class InvalidPriceException :
    DomainException("El precio no puede ser negativo")
