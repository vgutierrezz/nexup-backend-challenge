package main.kotlin.core.service

import main.kotlin.infraestructure.exception.SupermarketNotFoundException
import main.kotlin.core.domain.exception.ProductNotFoundException
import main.kotlin.core.domain.repository.ProductRepository
import main.kotlin.core.domain.repository.SupermarketRepository
import java.math.BigDecimal

/**
 * Registrar una venta de un producto
 * Dado un ID de producto y una cantidad a vender, se debe registrar la venta de un producto
 * La función debe retornar el precio total de la venta
 * @property repository Persistencia para acceder y actualizar datos del supermercado.
 * @property productRepository Persistencia para acceder a los datos de los productos.
 * @property supermarketId Identificador del local donde se realiza la operación.
 */

class RegisterSaleUserCase (
    private val repository: SupermarketRepository,
    private val productRepository: ProductRepository,
    private val supermarketId: Long
){

    fun execute(productId: Long, quantity: Int): BigDecimal {

        val supermarket = repository.getSupermarketById(supermarketId)
            ?: throw SupermarketNotFoundException(supermarketId)

        val product = productRepository.getById(productId)
            ?: throw ProductNotFoundException(productId)

        val saleId = repository.getNextSaleId()

        val total = supermarket.sellProduct(product, quantity, saleId)

        repository.updateSupermarket(supermarket)

        return total
    }
}