package main.kotlin.core.domain.exception

class ProductAlreadyExistsException(productId: Long):
    DomainException("El producto $productId ya existe en el supermercado")
