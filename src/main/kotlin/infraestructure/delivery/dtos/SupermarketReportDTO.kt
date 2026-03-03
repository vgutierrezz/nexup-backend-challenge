package main.kotlin.infraestructure.delivery.dtos

data class SupermarketReportDTO(
    val name: String,
    val id: Long,
    val totalRevenue: Double
)