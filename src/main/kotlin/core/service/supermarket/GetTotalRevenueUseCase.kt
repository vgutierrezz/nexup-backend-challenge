package main.kotlin.core.service.supermarket

import main.kotlin.core.domain.repository.SupermarketRepository
import main.kotlin.infraestructure.exception.SupermarketNotFoundException
import java.math.BigDecimal

/**
 * Obtener ingresos totales
 * Retornar el dinero total obtenido de todas las ventas realizadas
 * @property repository Repositorio para acceder a los datos del supermercado
 * @property supermarketId ID del supermercado para el cual se desea calcular el ingreso total
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
    fun execute(): BigDecimal {
        val supermarket = repository.getSupermarketById(this.supermarketId)
            ?: throw SupermarketNotFoundException(this.supermarketId)

        // Calculamos el total sumando totalPrice de todas las ventas
        return supermarket.getSales()
            .fold(BigDecimal.ZERO) { acc, sale -> acc + sale.totalPrice }
    }
}