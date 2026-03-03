package main.kotlin.core.service

import main.kotlin.core.domain.exception.SupermarketException.SupermarketNotFoundException
import main.kotlin.core.domain.repository.SupermarketRepository

/**
 * Caso de uso encargado de calcular la recaudación total acumulada por un
 * producto específico a través de todas sus ventas registradas.
 */
class GetProductRevenueUseCase (
    private val repository: SupermarketRepository,
    private val supermarketId: Long
) {
    /**
     * Calcula los ingresos totales generados por un producto.
     *
     * @param productId El identificador único del producto a consultar.
     * @return El monto total (Double) obtenido de las ventas de dicho producto.
     * @throws SupermarketNotFoundException si el local no existe.
     */
    fun execute(productId: Long): Double {
        val supermarket = repository.getSupermarketById(this.supermarketId)
            ?: throw SupermarketNotFoundException(this.supermarketId)

        // Filtramos la lista de ventas por el ID de producto y sumamos los montos totales registrados
        return supermarket.sales
            .filter { it.productId == productId }
            .sumOf { it.totalPrice }
    }
}