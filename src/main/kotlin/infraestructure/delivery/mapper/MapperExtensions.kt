package main.kotlin.infraestructure.delivery.mapper

import com.nexup.challenge.domain.models.Supermarket
import main.kotlin.infraestructure.delivery.dtos.SupermarketReportDTO

fun Supermarket.toReportDTO(revenue: Double): SupermarketReportDTO {
    return SupermarketReportDTO(
        id = this.id,
        name = this.name,
        totalRevenue = revenue
    )
}