package main.kotlin.core.domain.exception


class InsufficientStockException(productId: Long) :
    DomainException("Stock insuficiente para el producto $productId")