package main.kotlin.infraestructure.persistence


import main.kotlin.core.domain.models.Product
import main.kotlin.core.domain.models.Supermarket
import main.kotlin.core.domain.repository.SupermarketRepository
import java.math.BigDecimal
import java.util.concurrent.atomic.AtomicLong

class SuperMarketRepositoryImp : SupermarketRepository {
    private val supermarkets = mutableMapOf<Long, Supermarket>()

    // Contador global para las ventas de todos los supermercados
    private val saleIdCounter = AtomicLong(1)

    override fun getNextSaleId(): Long {
        return saleIdCounter.getAndIncrement()
    }

    override fun getSupermarketById(id: Long): Supermarket? = supermarkets[id]

    override fun updateSupermarket(supermarket: Supermarket) {
        supermarkets[supermarket.id] = supermarket
    }

    override fun getAllSupermarkets(): List<Supermarket> = supermarkets.values.toList()
    override fun addSupermarket(supermarket: Supermarket) {
        supermarkets[supermarket.id] = supermarket
    }
    override fun deleteSupermarket(id: Long) {
        supermarkets.remove(id)
    }

    override fun getSupermarketsByChainId(chainId: Long): List<Supermarket> {
        return supermarkets.values.filter { it.chainId == chainId }
    }

    override fun getProductById(productId: Long): Product? {
        for (supermarket in supermarkets.values) {
            val sp = supermarket.getProducts().find { it.productId == productId }
            if (sp != null) {
                return Product(
                    id = sp.productId,
                    name = "Nombre desconocido", // o nombre real de un catálogo
                    price = BigDecimal.ZERO      // o precio real si lo tenés
                )
            }
        }
        return null
    }
}