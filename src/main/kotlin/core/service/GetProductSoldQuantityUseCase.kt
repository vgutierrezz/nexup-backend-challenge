package main.kotlin.core.service

import main.kotlin.infraestructure.exception.SupermarketNotFoundException
import main.kotlin.core.domain.repository.SupermarketRepository

/**
 * Obtener la cantidad vendida de un producto
 * Dado un ID de producto, retornar la cantidad vendida de dicho producto
 * @property repository Persistencia para acceder a los datos del supermercado.
 * @property supermarketId Identificador del local donde se realiza la consulta.
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
     * @throws SupermarketNotFoundException si el local no existe.
     */
    fun execute(productId: Long): Int {
        val supermarket = repository.getSupermarketById(supermarketId)
            ?: throw SupermarketNotFoundException(supermarketId)

        return supermarket.getSoldQuantity(productId)
    }
}