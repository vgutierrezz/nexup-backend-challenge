package main.kotlin.core.service.supermarket

import main.kotlin.infraestructure.exception.SupermarketNotFoundException
import main.kotlin.core.domain.repository.SupermarketRepository
import java.math.BigDecimal

/**
 * Obtener ingresos por ventas de un producto
 * Dado un ID de producto, retornar el dinero obtenido de las ventas de dicho producto
 * @property repository Persistencia para acceder a los datos del supermercado.
 * @property supermarketId Identificador del local donde se realiza la consulta.
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
    fun execute(productId: Long): BigDecimal {
        val supermarket = repository.getSupermarketById(this.supermarketId)
            ?: throw SupermarketNotFoundException(this.supermarketId)

        return supermarket.getSales().asSequence()
            .filter { it.productId == productId }
            .sumOf { it.totalPrice }
    }
}