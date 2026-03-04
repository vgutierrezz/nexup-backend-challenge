package main.kotlin.core.supermarketChain

import main.kotlin.core.domain.repository.SupermarketRepository
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

class GetSupermarketHighestRevenueUseCase (
    private val repository: SupermarketRepository
){
    /**
     * Retorna el supermercado con mayores ingresos por ventas en toda la cadena,
     * en formato "<nombre> (<id>). Ingresos totales: <ingresos>".
     * @param repository Repositorio para acceder a los supermercados
     * @return String formateado, o "No hay supermercados" si la cadena está vacía
     */
    fun execute(): String {
        val allSupermarkets = repository.getAllSupermarkets()
        if (allSupermarkets.isEmpty()) return "No hay supermercados"

        // Encontrar supermercado con mayor ingreso
        val topSupermarket = allSupermarkets.maxByOrNull { supermarket ->
            supermarket.getSales().fold(BigDecimal.ZERO) { acc, sale -> acc + sale.totalPrice }
        }!!

        // Calcular ingresos totales
        val totalRevenue = topSupermarket.getSales()
            .fold(BigDecimal.ZERO) { acc, sale -> acc + sale.totalPrice }

        // Formatear moneda local
        val formattedRevenue = NumberFormat.getCurrencyInstance(Locale.US).format(totalRevenue)

        return "${topSupermarket.name} (${topSupermarket.id}). Ingresos totales: $formattedRevenue"
    }
}