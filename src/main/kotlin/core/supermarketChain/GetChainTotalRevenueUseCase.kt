package main.kotlin.core.supermarketChain

import main.kotlin.core.domain.repository.SupermarketRepository
import java.math.BigDecimal

class GetChainTotalRevenueUseCase(
    private val repository: SupermarketRepository
) {
    /**
     * Calcula el ingreso total de todas las ventas en toda la cadena de supermercados.
     *
     * @return BigDecimal con el total acumulado
     */
    fun execute(): BigDecimal {
        val allSales = repository.getAllSupermarkets()
            .flatMap { it.getSales() }

        return allSales.fold(BigDecimal.ZERO) { acc, sale ->
            acc + sale.totalPrice
        }
    }
}