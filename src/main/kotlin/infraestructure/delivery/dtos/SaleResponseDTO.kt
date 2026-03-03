package main.kotlin.infraestructure.delivery.dtos

data class SaleResponseDTO(
    val productName: String,
    val quantity: Int,
    val totalPrice: Double
)