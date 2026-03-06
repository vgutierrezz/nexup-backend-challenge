package com.nexup.challenge.domain.models

import main.kotlin.core.domain.exception.InsufficientStockException
import main.kotlin.core.domain.exception.InvalidQuantityException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class SupermarketProductTest {

    @Test
    fun `crear producto de supermercado con stock válido`() {
        val product = SupermarketProduct(productId = 1L, stock = 10)

        assertEquals(1L, product.productId)
        assertEquals(10, product.currentStock())
    }

    @Test
    fun `crear producto de supermercado con stock cero`() {
        val product = SupermarketProduct(productId = 1L, stock = 0)

        assertEquals(0, product.currentStock())
    }

    @Test
    fun `crear producto de supermercado con stock negativo lanza InvalidQuantityException`() {
        assertThrows(InvalidQuantityException::class.java) {
            SupermarketProduct(productId = 1L, stock = -5)
        }
    }

    @Test
    fun `increaseStock suma correctamente la cantidad`() {
        val product = SupermarketProduct(productId = 1L, stock = 10)
        product.increaseStock(5)

        assertEquals(15, product.currentStock())
    }

    @Test
    fun `increaseStock con cantidad cero lanza InvalidQuantityException`() {
        val product = SupermarketProduct(productId = 1L, stock = 10)

        assertThrows(InvalidQuantityException::class.java) {
            product.increaseStock(0)
        }
    }

    @Test
    fun `increaseStock con cantidad negativa lanza InvalidQuantityException`() {
        val product = SupermarketProduct(productId = 1L, stock = 10)

        assertThrows(InvalidQuantityException::class.java) {
            product.increaseStock(-5)
        }
    }

    @Test
    fun `decreaseStock resta correctamente la cantidad`() {
        val product = SupermarketProduct(productId = 1L, stock = 10)
        product.decreaseStock(3)

        assertEquals(7, product.currentStock())
    }

    @Test
    fun `decreaseStock con cantidad cero lanza InvalidQuantityException`() {
        val product = SupermarketProduct(productId = 1L, stock = 10)

        assertThrows(InvalidQuantityException::class.java) {
            product.decreaseStock(0)
        }
    }

    @Test
    fun `decreaseStock con cantidad negativa lanza InvalidQuantityException`() {
        val product = SupermarketProduct(productId = 1L, stock = 10)

        assertThrows(InvalidQuantityException::class.java) {
            product.decreaseStock(-5)
        }
    }

    @Test
    fun `decreaseStock con cantidad mayor al stock lanza InsufficientStockException`() {
        val product = SupermarketProduct(productId = 1L, stock = 5)

        assertThrows(InsufficientStockException::class.java) {
            product.decreaseStock(10)
        }
    }

    @Test
    fun `decreaseStock con cantidad igual al stock deja stock en cero`() {
        val product = SupermarketProduct(productId = 1L, stock = 5)
        product.decreaseStock(5)

        assertEquals(0, product.currentStock())
    }

    @Test
    fun `hasStock retorna true cuando hay suficiente stock`() {
        val product = SupermarketProduct(productId = 1L, stock = 10)

        assertTrue(product.hasStock(5))
        assertTrue(product.hasStock(10))
    }

    @Test
    fun `hasStock retorna false cuando no hay suficiente stock`() {
        val product = SupermarketProduct(productId = 1L, stock = 5)

        assertFalse(product.hasStock(10))
    }

    @Test
    fun `hasStock retorna true cuando stock es cero y se pregunta por cero`() {
        val product = SupermarketProduct(productId = 1L, stock = 0)

        assertTrue(product.hasStock(0))
    }

    @Test
    fun `múltiples operaciones de stock se acumulan correctamente`() {
        val product = SupermarketProduct(productId = 1L, stock = 10)
        product.increaseStock(5)
        product.decreaseStock(3)
        product.increaseStock(8)
        product.decreaseStock(2)

        assertEquals(18, product.currentStock())
    }
}

