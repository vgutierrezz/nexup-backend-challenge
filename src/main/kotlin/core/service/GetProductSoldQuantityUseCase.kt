package main.kotlin.core.service

import main.kotlin.core.domain.exception.SupermarketException.SupermarketNotFoundException
import main.kotlin.core.domain.repository.SupermarketRepository

/**
 * Caso de uso encargado de consultar la traccionalidad histórica de un producto
 * para determinar la cantidad total de unidades vendidas en un supermercado.
 */
class GetProductSoldQuantityUseCase (
    private val repository: SupermarketRepository,
    private val supermarketId: Long
){
    /**
     * Obtiene la cantidad total vendida de un producto específico.
     *
     * @param productId El identificador único del producto a consultar.
     * @return La sumatoria de unidades vendidas a través de todas las transacciones registradas.
     * @throws SupermarketException.SupermarketNotFoundException si el local no existe.
     */
    fun execute(productId: Long): Int {
        val supermarket = repository.getSupermarketById(this.supermarketId)
            ?: throw SupermarketNotFoundException(this.supermarketId)

        // Filtramos las ventas por el ID del producto y sumamos las cantidades
        return supermarket.sales
            .filter { it.productId == productId }
            .sumOf { it.quantity }
    }
}