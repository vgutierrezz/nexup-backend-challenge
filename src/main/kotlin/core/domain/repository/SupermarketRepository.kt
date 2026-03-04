package main.kotlin.core.domain.repository

import main.kotlin.core.domain.models.Supermarket


interface SupermarketRepository {
    fun getAllSupermarkets(): List<Supermarket>
    fun getSupermarketById(id: Long): Supermarket?
    fun addSupermarket(supermarket: Supermarket)
    fun updateSupermarket(supermarket: Supermarket)
    fun deleteSupermarket(id: Long)
    fun getNextSaleId(): Long
    fun getSupermarketsByChainId(chainId: Long): List<Supermarket>
}