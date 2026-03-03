package main.kotlin.core.domain.models

import java.time.LocalDate

data class Sale (
    val id: Long,
    val productId: Long,
    val quantity: Int,
    val supermarketId: Long,
    val totalPrice: Double,
    val timestamp: LocalDate = LocalDate.now()
)