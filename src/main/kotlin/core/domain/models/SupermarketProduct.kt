package com.nexup.challenge.domain.models


import main.kotlin.core.domain.exception.*

//Entidad intermedia
class SupermarketProduct(
    val productId: Long,
    private var stock: Int
) {
    //Validaciones
    init {
        if(stock < 0) { throw InvalidQuantityException() }
    }

    fun increaseStock(quantity: Int) {
        if (quantity <= 0) { throw InvalidQuantityException() }
        stock += quantity
    }

    fun decreaseStock(quantity: Int) {
        if (quantity <= 0) { throw InvalidQuantityException() }
        if (quantity > stock) { throw InsufficientStockException(productId) }
        stock -= quantity
    }

    fun hasStock(quantity: Int): Boolean = stock >= quantity

    fun currentStock(): Int = stock
}