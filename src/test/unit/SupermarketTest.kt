package main.kotlin.core.domain.models

import main.kotlin.core.domain.exception.InvalidNameException
import main.kotlin.core.domain.exception.ProductAlreadyExistsException
import main.kotlin.core.domain.exception.ProductNotFoundException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import java.math.BigDecimal

class SupermarketTest {

    private lateinit var supermarket: Supermarket
    private lateinit var product: Product
    private lateinit var openHours: OpenHours

    @BeforeEach
    fun setUp() {
        product = Product(
            id = 1L,
            name = "Leche",
            price = BigDecimal("2.50")
        )

        openHours = OpenHours(
            daysOpen = setOf(java.time.DayOfWeek.MONDAY, java.time.DayOfWeek.TUESDAY),
            openTime = java.time.LocalTime.of(9, 0),
            closeTime = java.time.LocalTime.of(18, 0)
        )

        supermarket = Supermarket(
            id = 100L,
            name = "Supermercado Central",
            chainId = 1L,
            hours = openHours
        )
    }

    @Test
    fun `crear supermercado con datos válidos`() {
        assertEquals(100L, supermarket.id)
        assertEquals("Supermercado Central", supermarket.name)
        assertEquals(1L, supermarket.chainId)
        assertEquals(openHours, supermarket.hours)
    }

    @Test
    fun `crear supermercado con nombre vacío lanza InvalidNameException`() {
        assertThrows(InvalidNameException::class.java) {
            Supermarket(
                id = 100L,
                name = "",
                chainId = 1L
            )
        }
    }

    @Test
    fun `crear supermercado con nombre en blanco lanza InvalidNameException`() {
        assertThrows(InvalidNameException::class.java) {
            Supermarket(
                id = 100L,
                name = "   ",
                chainId = 1L
            )
        }
    }

    @Test
    fun `addProduct agrega un producto correctamente`() {
        supermarket.addProduct(productId = 1L, initialStock = 10)

        assertEquals(1, supermarket.getProducts().size)
        assertEquals(1L, supermarket.getProducts()[0].productId)
        assertEquals(10, supermarket.getProducts()[0].currentStock())
    }

    @Test
    fun `addProduct con un producto que ya existe lanza ProductAlreadyExistsException`() {
        supermarket.addProduct(productId = 1L, initialStock = 10)

        assertThrows(ProductAlreadyExistsException::class.java) {
            supermarket.addProduct(productId = 1L, initialStock = 5)
        }
    }

    @Test
    fun `increaseStock incrementa el stock del producto correctamente`() {
        supermarket.addProduct(productId = 1L, initialStock = 10)
        supermarket.increaseStock(productId = 1L, quantity = 5)

        assertEquals(15, supermarket.getProducts()[0].currentStock())
    }

    @Test
    fun `increaseStock de producto inexistente lanza ProductNotFoundException`() {
        assertThrows(ProductNotFoundException::class.java) {
            supermarket.increaseStock(productId = 999L, quantity = 5)
        }
    }

    @Test
    fun `sellProduct realiza la venta correctamente y retorna el precio total`() {
        supermarket.addProduct(productId = product.id, initialStock = 20)

        val totalPrice = supermarket.sellProduct(product = product, quantity = 5, saleId = 1L)

        assertEquals(BigDecimal("12.50"), totalPrice)
        assertEquals(15, supermarket.getProducts()[0].currentStock())
        assertEquals(1, supermarket.getSales().size)
    }

    @Test
    fun `sellProduct registra la venta correctamente`() {
        supermarket.addProduct(productId = product.id, initialStock = 20)
        supermarket.sellProduct(product = product, quantity = 5, saleId = 1L)

        val sales = supermarket.getSales()
        assertEquals(1, sales.size)
        assertEquals(1L, sales[0].id)
        assertEquals(product.id, sales[0].productId)
        assertEquals(5, sales[0].quantity)
    }

    @Test
    fun `sellProduct sin stock suficiente lanza InsufficientStockException`() {
        supermarket.addProduct(productId = product.id, initialStock = 5)

        assertThrows(main.kotlin.core.domain.exception.InsufficientStockException::class.java) {
            supermarket.sellProduct(product = product, quantity = 10, saleId = 1L)
        }
    }

    @Test
    fun `getSoldQuantity calcula correctamente la cantidad vendida`() {
        supermarket.addProduct(productId = product.id, initialStock = 100)
        supermarket.sellProduct(product = product, quantity = 5, saleId = 1L)
        supermarket.sellProduct(product = product, quantity = 10, saleId = 2L)

        val soldQuantity = supermarket.getSoldQuantity(product.id)
        assertEquals(15, soldQuantity)
    }

    @Test
    fun `getSoldQuantity con producto sin ventas retorna cero`() {
        supermarket.addProduct(productId = product.id, initialStock = 100)

        val soldQuantity = supermarket.getSoldQuantity(product.id)
        assertEquals(0, soldQuantity)
    }

    @Test
    fun `getProducts retorna lista de productos`() {
        supermarket.addProduct(productId = 1L, initialStock = 10)
        supermarket.addProduct(productId = 2L, initialStock = 20)

        val products = supermarket.getProducts()
        assertEquals(2, products.size)
    }

    @Test
    fun `getSales retorna lista de ventas`() {
        supermarket.addProduct(productId = product.id, initialStock = 100)
        supermarket.sellProduct(product = product, quantity = 5, saleId = 1L)
        supermarket.sellProduct(product = product, quantity = 10, saleId = 2L)

        val sales = supermarket.getSales()
        assertEquals(2, sales.size)
    }

    @Test
    fun `crear supermercado sin horarios es válido`() {
        val supermarketSinHours = Supermarket(
            id = 101L,
            name = "Otro Supermercado",
            chainId = 1L
        )

        assertNull(supermarketSinHours.hours)
    }

    @Test
    fun `múltiples operaciones de venta funcionan correctamente`() {
        supermarket.addProduct(productId = product.id, initialStock = 100)

        val total1 = supermarket.sellProduct(product = product, quantity = 5, saleId = 1L)
        val total2 = supermarket.sellProduct(product = product, quantity = 10, saleId = 2L)
        val total3 = supermarket.sellProduct(product = product, quantity = 3, saleId = 3L)

        assertEquals(BigDecimal("12.50"), total1)
        assertEquals(BigDecimal("25.00"), total2)
        assertEquals(BigDecimal("7.50"), total3)
        assertEquals(82, supermarket.getProducts()[0].currentStock())
        assertEquals(18, supermarket.getSoldQuantity(product.id))
    }
}

