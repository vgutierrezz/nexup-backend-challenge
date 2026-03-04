package main.kotlin.core.service

import main.kotlin.core.domain.models.Product
import main.kotlin.core.domain.repository.SupermarketRepository

class GetTop5ProductsUseCase(
    private val repository: SupermarketRepository
) {
    /**
     * Obtiene los 5 productos más vendidos en toda la cadena de supermercados.
     * @param topN número de productos más vendidos a retornar
     * @return String con formato "<nombre>:cantidad - <nombre>:cantidad - ..."
     */
    fun execute(topN: Int = 5): String {
        // Obtener todas las ventas de todos los supermercados
        val allSales = repository.getAllSupermarkets()
            .flatMap { it.getSales() }

        // Agrupar cantidad vendida por productId
        val salesByProduct = allSales.groupingBy { it.productId }
            .fold(0) { acc, sale -> acc + sale.quantity }

        if (salesByProduct.isEmpty()) return ""

        // Construir un mapa productId -> Product para obtener nombres
        val productIds = salesByProduct.keys
        val productMap: Map<Long, Product?> = productIds.associateWith { repository.getProductById(it) }

        // Ordenar por cantidad descendente y tomar top N
        val topProducts = salesByProduct.entries
            .sortedByDescending { it.value }
            .take(topN)

        // Mapear a "<nombre>: cantidad"
        val topProductsStrings = topProducts.map { entry ->
            val name = productMap[entry.key]?.name ?: "Unknown"
            "$name: ${entry.value}"
        }

        return topProductsStrings.joinToString(" - ")
    }
}