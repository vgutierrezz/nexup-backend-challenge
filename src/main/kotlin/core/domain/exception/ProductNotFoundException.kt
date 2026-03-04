package main.kotlin.core.domain.exception


class ProductNotFoundException(productId: Long) :
    DomainException("Producto con ID $productId no encontrado")