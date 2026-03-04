package main.kotlin.core.domain.models

import com.nexup.challenge.domain.models.SupermarketProduct
import main.kotlin.core.domain.exception.InvalidNameException
import main.kotlin.core.domain.exception.ProductAlreadyExistsException
import main.kotlin.core.domain.exception.ProductNotFoundException
import java.math.BigDecimal

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

    fun sellProduct(product: Product, quantity: Int, saleId: Long): BigDecimal {
        val supermarketProduct = findProduct(product.id)

        supermarketProduct.decreaseStock(quantity)

        val sale = Sale(
            id = saleId,
            productId = product.id,
            quantity = quantity,
            supermarketId = id,
            unitPrice = product.price
        )

        sales.add(sale)

        return sale.totalPrice
    }

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

    fun getSoldQuantity(productId: Long): Int {
        return sales
            .filter { it.productId == productId }
            .sumOf { it.quantity }
    }

    private fun findProduct(productId: Long): SupermarketProduct {
        return products.find { it.productId == productId }
            ?: throw ProductNotFoundException(productId)
    }
}