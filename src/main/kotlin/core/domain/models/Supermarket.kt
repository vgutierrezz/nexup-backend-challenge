package main.kotlin.core.domain.models

import com.nexup.challenge.domain.models.SupermarketProduct
import main.kotlin.core.domain.exception.InvalidNameException
import main.kotlin.core.domain.exception.ProductAlreadyExistsException
import main.kotlin.core.domain.exception.ProductNotFoundException

class Supermarket(
    val id: Long,
    val name: String,
    val chainId: Long,
    //Collections privadas para evitar modificaciones externas
    private val products: MutableList<SupermarketProduct> = mutableListOf(),
    private val sales: MutableList<Sale> = mutableListOf(),
    val hours: OpenHours? = null
) {
    init {
        if (name.isBlank()) {
            throw InvalidNameException()
        }
    }
    // Exposición controlada
    fun getProducts(): List<SupermarketProduct> = products.toList()

    fun getSales(): List<Sale> = sales.toList()

    //Comportamiento del dominio
    fun addProduct(productId: Long, initialStock: Int) {
        if (products.any { it.productId == productId }) {
            throw ProductAlreadyExistsException(productId)
        }
        products.add(SupermarketProduct(productId, initialStock))
    }

    fun increaseStock(productId: Long, quantity: Int) {
        val product = findProduct(productId)
        product.increaseStock(quantity)
    }

    //Método Privado
    private fun findProduct(productId: Long): SupermarketProduct {
        return products.find { it.productId == productId }
            ?: throw ProductNotFoundException(productId)
    }
}