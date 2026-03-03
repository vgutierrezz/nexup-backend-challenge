package main.kotlin.core.domain.repository

import com.nexup.challenge.domain.models.Supermarket

interface SupermarketRepository {
    fun getAllSupermarkets(): List<Supermarket>
    fun getSupermarketById(id: Long): Supermarket?
    fun addSupermarket(supermarket: Supermarket)
    fun updateSupermarket(supermarket: Supermarket)
    fun deleteSupermarket(id: Long)
}