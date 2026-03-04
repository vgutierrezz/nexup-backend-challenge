package main.kotlin.infraestructure.persistence

import com.nexup.challenge.domain.models.Supermarket
import main.kotlin.core.domain.repository.SupermarketRepository
import java.util.concurrent.atomic.AtomicLong

class InMemorySupermarketRepository : SupermarketRepository {
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
}