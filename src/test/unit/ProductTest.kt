package main.kotlin.core.domain.models

import main.kotlin.core.domain.exception.InvalidPriceException
import main.kotlin.core.domain.exception.InvalidProductNameException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.math.BigDecimal

class ProductTest {

    @Test
    fun `crear producto con datos válidos`() {
        val product = Product(
            id = 1L,
            name = "Leche",
            price = BigDecimal("2.50")
        )

        assertEquals(1L, product.id)
        assertEquals("Leche", product.name)
        assertEquals(BigDecimal("2.50"), product.price)
    }

    @Test
    fun `crear producto con nombre vacío lanza InvalidProductNameException`() {
        assertThrows(InvalidProductNameException::class.java) {
            Product(
                id = 1L,
                name = "",
                price = BigDecimal("2.50")
            )
        }
    }

    @Test
    fun `crear producto con nombre en blanco lanza InvalidProductNameException`() {
        assertThrows(InvalidProductNameException::class.java) {
            Product(
                id = 1L,
                name = "   ",
                price = BigDecimal("2.50")
            )
        }
    }

    @Test
    fun `crear producto con precio negativo lanza InvalidPriceException`() {
        assertThrows(InvalidPriceException::class.java) {
            Product(
                id = 1L,
                name = "Leche",
                price = BigDecimal("-1.00")
            )
        }
    }

    @Test
    fun `crear producto con precio cero es válido`() {
        val product = Product(
            id = 1L,
            name = "Promoción",
            price = BigDecimal.ZERO
        )

        assertEquals(BigDecimal.ZERO, product.price)
    }

    @Test
    fun `crear producto con precio positivo es válido`() {
        val product = Product(
            id = 1L,
            name = "Café",
            price = BigDecimal("5.99")
        )

        assertEquals(BigDecimal("5.99"), product.price)
    }
}

