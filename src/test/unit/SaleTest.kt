package main.kotlin.core.domain.models

import main.kotlin.core.domain.exception.InvalidPriceException
import main.kotlin.core.domain.exception.InvalidQuantityException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.math.BigDecimal
import java.time.LocalDate

class SaleTest {

    @Test
    fun `crear venta con datos válidos`() {
        val sale = Sale(
            id = 1L,
            productId = 10L,
            quantity = 5,
            supermarketId = 100L,
            unitPrice = BigDecimal("2.50"),
            timestamp = LocalDate.of(2025, 3, 4)
        )

        assertEquals(1L, sale.id)
        assertEquals(10L, sale.productId)
        assertEquals(5, sale.quantity)
        assertEquals(100L, sale.supermarketId)
        assertEquals(BigDecimal("2.50"), sale.unitPrice)
        assertEquals(LocalDate.of(2025, 3, 4), sale.timestamp)
    }

    @Test
    fun `totalPrice calcula correctamente el precio total`() {
        val sale = Sale(
            id = 1L,
            productId = 10L,
            quantity = 5,
            supermarketId = 100L,
            unitPrice = BigDecimal("2.50")
        )

        assertEquals(BigDecimal("12.50"), sale.totalPrice)
    }

    @Test
    fun `totalPrice con cantidad 1 retorna el unitPrice`() {
        val sale = Sale(
            id = 1L,
            productId = 10L,
            quantity = 1,
            supermarketId = 100L,
            unitPrice = BigDecimal("10.00")
        )

        assertEquals(BigDecimal("10.00"), sale.totalPrice)
    }

    @Test
    fun `totalPrice con unitPrice cero retorna cero`() {
        val sale = Sale(
            id = 1L,
            productId = 10L,
            quantity = 5,
            supermarketId = 100L,
            unitPrice = BigDecimal.ZERO
        )

        assertEquals(BigDecimal.ZERO, sale.totalPrice)
    }

    @Test
    fun `crear venta con quantity cero lanza InvalidQuantityException`() {
        assertThrows(InvalidQuantityException::class.java) {
            Sale(
                id = 1L,
                productId = 10L,
                quantity = 0,
                supermarketId = 100L,
                unitPrice = BigDecimal("2.50")
            )
        }
    }

    @Test
    fun `crear venta con quantity negativa lanza InvalidQuantityException`() {
        assertThrows(InvalidQuantityException::class.java) {
            Sale(
                id = 1L,
                productId = 10L,
                quantity = -5,
                supermarketId = 100L,
                unitPrice = BigDecimal("2.50")
            )
        }
    }

    @Test
    fun `crear venta con unitPrice negativo lanza InvalidPriceException`() {
        assertThrows(InvalidPriceException::class.java) {
            Sale(
                id = 1L,
                productId = 10L,
                quantity = 5,
                supermarketId = 100L,
                unitPrice = BigDecimal("-2.50")
            )
        }
    }

    @Test
    fun `crear venta con unitPrice cero es válido`() {
        val sale = Sale(
            id = 1L,
            productId = 10L,
            quantity = 5,
            supermarketId = 100L,
            unitPrice = BigDecimal.ZERO
        )

        assertEquals(BigDecimal.ZERO, sale.unitPrice)
    }

    @Test
    fun `timestamp por defecto es la fecha actual`() {
        val today = LocalDate.now()
        val sale = Sale(
            id = 1L,
            productId = 10L,
            quantity = 5,
            supermarketId = 100L,
            unitPrice = BigDecimal("2.50")
        )

        assertEquals(today, sale.timestamp)
    }

    @Test
    fun `totalPrice con cantidad grande`() {
        val sale = Sale(
            id = 1L,
            productId = 10L,
            quantity = 1000,
            supermarketId = 100L,
            unitPrice = BigDecimal("99.99")
        )

        assertEquals(BigDecimal("99990.00"), sale.totalPrice)
    }
}

