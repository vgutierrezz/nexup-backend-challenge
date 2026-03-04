package main.kotlin.core.domain.models

import main.kotlin.core.domain.exception.InvalidPriceException
import main.kotlin.core.domain.exception.InvalidQuantityException
import java.math.BigDecimal
import java.time.LocalDate

class Sale (
    val id: Long,
    val productId: Long,
    val quantity: Int,
    val supermarketId: Long,
    val unitPrice: BigDecimal,
    val timestamp: LocalDate = LocalDate.now()
){
    //Validaciones
    init {
        if (quantity <= 0) {
            throw InvalidQuantityException()
        }

        if (unitPrice < BigDecimal.ZERO) {
            throw InvalidPriceException()
        }
    }

    //Guardo el precio unitario y calculo el precio total
    val totalPrice: BigDecimal
        get() = unitPrice.multiply(BigDecimal.valueOf(quantity.toLong()))
}