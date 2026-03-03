package main.kotlin.core.service

import main.kotlin.core.domain.exception.SupermarketException.SupermarketNotFoundException
import main.kotlin.core.domain.repository.SupermarketRepository

/**
 * Caso de uso encargado de calcular la facturación total de un supermercado,
 * sumando el importe de todas las ventas realizadas sin distinción de producto.
 */
class GetTotalRevenueUseCase (
    private val repository: SupermarketRepository,
    private val supermarketId: Long
){
    /**
     * Calcula el ingreso total acumulado por el supermercado.
     *
     * @return La sumatoria total (Double) de todas las ventas en el historial.
     * @throws SupermarketNotFoundException si el local no existe.
     */
    fun execute(): Double {
        val supermarket = repository.getSupermarketById(this.supermarketId)
            ?: throw SupermarketNotFoundException(this.supermarketId)

        // Sumamos el totalPrice de todas las ventas registradas
        return supermarket.sales.sumOf { it.totalPrice }
    }
}