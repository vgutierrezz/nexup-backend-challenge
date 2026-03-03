package main.kotlin.core.domain.exception

sealed class SupermarketException(message: String) : Exception(message) {
    class ProductNotFoundException(id: Long) : SupermarketException("Producto con ID $id no encontrado")
    class InsufficientStockException(name: String) : SupermarketException("Stock insuficiente para: $name")
    class SupermarketNotFoundException(id: Long) : SupermarketException("Supermercado $id no encontrado")
}