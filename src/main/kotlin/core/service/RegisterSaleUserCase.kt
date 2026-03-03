package main.kotlin.core.service

import main.kotlin.core.domain.exception.SupermarketException
import main.kotlin.core.domain.models.Sale
import main.kotlin.core.domain.repository.SupermarketRepository

/**
 * Caso de uso encargado de procesar la venta de un producto.
 * @property repository Persistencia para acceder y actualizar datos del supermercado.
 * @property supermarketId Identificador del local donde se realiza la operación.
 */

class RegisterSaleUserCase (
    private val repository: SupermarketRepository,
    private val supermarketId: Long //inyectamos el ID del supermercado
){

    /**
     * Ejecuta la lógica de validación de stock y registro de venta.
     * @param productId ID del producto a vender.
     * @param quantity Cantidad de unidades solicitadas.
     * @return El monto total de la venta realizada.
     * @throws SupermarketException.InsufficientStockException Si no hay stock suficiente.
     * @throws SupermarketException.ProductNotFoundException Si el producto no existe en el local.
     */
    fun execute(productId: Long, quantity: Int): Double {
        // Buscar el supermercado por su id
        val supermarket = repository.getSupermarketById(supermarketId)
            ?: throw SupermarketException.SupermarketNotFoundException(supermarketId)

        //Buscar el producto en el catálogo del supermercado
        val product = supermarket.products.find { it.id == productId }
            ?: throw SupermarketException.ProductNotFoundException(productId)

        //Validar el Stock
        val currentStock = supermarket.stock[productId] ?: 0
        if (currentStock < quantity) {
            throw SupermarketException.InsufficientStockException(product.name)
        }

        //Aplicar cambios en el stock
        supermarket.stock[productId] = currentStock - quantity
        val totalAmount = product.price * quantity

        //Creación de la entidad Sale
        val nextId = repository.getNextSaleId() //Id autoincremental
        supermarket.sales.add(
            Sale(
                nextId,
                productId,
                quantity,
                supermarketId,
                totalAmount
            )
        )

        //Persistir los cambios
        repository.updateSupermarket(supermarket)

        return totalAmount
    }
}