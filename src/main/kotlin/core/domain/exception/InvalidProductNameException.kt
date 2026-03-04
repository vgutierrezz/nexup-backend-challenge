package main.kotlin.core.domain.exception

class InvalidProductNameException :
    DomainException("El nombre del producto no puede estar vacío")
