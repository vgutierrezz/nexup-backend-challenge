package test.integration

import main.kotlin.core.domain.models.OpenHours
import main.kotlin.core.domain.models.Product
import main.kotlin.core.domain.models.Supermarket
import main.kotlin.core.service.supermarket.RegisterSaleUseCase
import main.kotlin.core.service.supermarket.GetProductRevenueUseCase
import main.kotlin.core.service.supermarket.GetTotalRevenueUseCase
import main.kotlin.infraestructure.persistence.ProductRepositoryImp
import main.kotlin.infraestructure.persistence.SuperMarketRepositoryImp
import main.kotlin.core.domain.exception.InsufficientStockException
import main.kotlin.core.domain.exception.ProductNotFoundException
import main.kotlin.core.domain.exception.ProductAlreadyExistsException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.DayOfWeek
import java.time.LocalTime

class SupermarketBusinessRulesIntegrationTest {

    private lateinit var supermarketRepository: SuperMarketRepositoryImp
    private lateinit var productRepository: ProductRepositoryImp

    @BeforeEach
    fun setUp() {
        supermarketRepository = SuperMarketRepositoryImp()
        productRepository = ProductRepositoryImp()

        // Productos base
        productRepository.save(Product(1, "Laptop", 1000.toBigDecimal()))
        productRepository.save(Product(2, "Mouse", 25.toBigDecimal()))
        productRepository.save(Product(3, "Teclado", 75.toBigDecimal()))
        productRepository.save(Product(4, "Monitor", 300.toBigDecimal()))
        productRepository.save(Product(5, "USB Cable", 10.toBigDecimal()))
    }

    @Nested
    inner class StockLimitTests {

        @Test
        fun `debe permitir vender todo el stock disponible`() {
            val supermarket = Supermarket(id = 1, name = "TechStore", chainId = 1)
            supermarket.addProduct(productId = 1, initialStock = 10)
            supermarketRepository.addSupermarket(supermarket)

            val registerSale = RegisterSaleUseCase(supermarketRepository, productRepository, 1)

            // Vender todo el stock
            val total = registerSale.execute(productId = 1, quantity = 10)

            assertEquals(10000.toBigDecimal(), total)
            assertEquals(0, supermarket.getProducts().first().currentStock())
        }

        @Test
        fun `debe lanzar excepción al intentar vender más del stock disponible`() {
            val supermarket = Supermarket(id = 1, name = "TechStore", chainId = 1)
            supermarket.addProduct(productId = 1, initialStock = 5)
            supermarketRepository.addSupermarket(supermarket)

            val registerSale = RegisterSaleUseCase(supermarketRepository, productRepository, 1)

            assertThrows(InsufficientStockException::class.java) {
                registerSale.execute(productId = 1, quantity = 6)
            }
        }

        @Test
        fun `debe prevenir ventas cuando el stock llega a cero`() {
            val supermarket = Supermarket(id = 1, name = "TechStore", chainId = 1)
            supermarket.addProduct(productId = 2, initialStock = 10)
            supermarketRepository.addSupermarket(supermarket)

            val registerSale = RegisterSaleUseCase(supermarketRepository, productRepository, 1)

            registerSale.execute(productId = 2, quantity = 10)

            assertThrows(InsufficientStockException::class.java) {
                registerSale.execute(productId = 2, quantity = 1)
            }
        }

        @Test
        fun `debe permitir reponer stock y vender nuevamente`() {
            val supermarket = Supermarket(id = 1, name = "TechStore", chainId = 1)
            supermarket.addProduct(productId = 2, initialStock = 5)
            supermarketRepository.addSupermarket(supermarket)

            val registerSale = RegisterSaleUseCase(supermarketRepository, productRepository, 1)

            // Vender todo
            registerSale.execute(productId = 2, quantity = 5)

            // Reponer stock
            supermarket.increaseStock(productId = 2, quantity = 10)

            // Vender nuevamente
            val total = registerSale.execute(productId = 2, quantity = 8)
            assertEquals(200.toBigDecimal(), total)
        }
    }

    @Nested
    inner class ProductManagementTests {

        @Test
        fun `debe permitir agregar múltiples productos al supermercado`() {
            val supermarket = Supermarket(id = 1, name = "TechStore", chainId = 1)
            supermarketRepository.addSupermarket(supermarket)

            supermarket.addProduct(productId = 1, initialStock = 10)
            supermarket.addProduct(productId = 2, initialStock = 50)
            supermarket.addProduct(productId = 3, initialStock = 30)
            supermarket.addProduct(productId = 4, initialStock = 15)

            assertEquals(4, supermarket.getProducts().size)
        }

        @Test
        fun `debe lanzar excepción al agregar producto duplicado`() {
            val supermarket = Supermarket(id = 1, name = "TechStore", chainId = 1)
            supermarket.addProduct(productId = 1, initialStock = 10)
            supermarketRepository.addSupermarket(supermarket)

            assertThrows(ProductAlreadyExistsException::class.java) {
                supermarket.addProduct(productId = 1, initialStock = 5)
            }
        }

        @Test
        fun `debe incrementar correctamente el stock de un producto existente`() {
            val supermarket = Supermarket(id = 1, name = "TechStore", chainId = 1)
            supermarket.addProduct(productId = 1, initialStock = 10)
            supermarketRepository.addSupermarket(supermarket)

            supermarket.increaseStock(productId = 1, quantity = 20)

            val stock = supermarket.getProducts().first { it.productId == 1L }.currentStock()
            assertEquals(30, stock)
        }

        @Test
        fun `debe lanzar excepción al incrementar stock de producto inexistente`() {
            val supermarket = Supermarket(id = 1, name = "TechStore", chainId = 1)
            supermarketRepository.addSupermarket(supermarket)

            assertThrows(ProductNotFoundException::class.java) {
                supermarket.increaseStock(productId = 999, quantity = 10)
            }
        }
    }

    @Nested
    inner class SalesTrackingTests {

        @Test
        fun `debe rastrear todas las ventas realizadas en el supermercado`() {
            val supermarket = Supermarket(id = 1, name = "TechStore", chainId = 1)
            supermarket.addProduct(productId = 1, initialStock = 100)
            supermarket.addProduct(productId = 2, initialStock = 100)
            supermarketRepository.addSupermarket(supermarket)

            val registerSale = RegisterSaleUseCase(supermarketRepository, productRepository, 1)

            registerSale.execute(productId = 1, quantity = 5)
            registerSale.execute(productId = 2, quantity = 10)
            registerSale.execute(productId = 1, quantity = 3)

            assertEquals(3, supermarket.getSales().size)
        }

        @Test
        fun `debe calcular correctamente la cantidad total vendida de un producto`() {
            val supermarket = Supermarket(id = 1, name = "TechStore", chainId = 1)
            supermarket.addProduct(productId = 1, initialStock = 100)
            supermarketRepository.addSupermarket(supermarket)

            val registerSale = RegisterSaleUseCase(supermarketRepository, productRepository, 1)

            registerSale.execute(productId = 1, quantity = 5)
            registerSale.execute(productId = 1, quantity = 8)
            registerSale.execute(productId = 1, quantity = 12)

            val soldQuantity = supermarket.getSoldQuantity(productId = 1)
            assertEquals(25, soldQuantity)
        }

        @Test
        fun `debe mantener historial de precios en cada venta`() {
            val supermarket = Supermarket(id = 1, name = "TechStore", chainId = 1)
            supermarket.addProduct(productId = 2, initialStock = 100)
            supermarketRepository.addSupermarket(supermarket)

            val registerSale = RegisterSaleUseCase(supermarketRepository, productRepository, 1)

            registerSale.execute(productId = 2, quantity = 5)

            val sale = supermarket.getSales().first()
            assertEquals(25.toBigDecimal(), sale.unitPrice)
            assertEquals(125.toBigDecimal(), sale.totalPrice)
        }

        @Test
        fun `debe generar IDs únicos para cada venta`() {
            val supermarket = Supermarket(id = 1, name = "TechStore", chainId = 1)
            supermarket.addProduct(productId = 1, initialStock = 100)
            supermarketRepository.addSupermarket(supermarket)

            val registerSale = RegisterSaleUseCase(supermarketRepository, productRepository, 1)

            registerSale.execute(productId = 1, quantity = 1)
            registerSale.execute(productId = 1, quantity = 1)
            registerSale.execute(productId = 1, quantity = 1)

            val saleIds = supermarket.getSales().map { it.id }.toSet()
            assertEquals(3, saleIds.size)  // Todos los IDs deben ser únicos
        }
    }

    @Nested
    inner class RevenueCalculationTests {

        @Test
        fun `debe calcular ingresos de un producto con múltiples ventas`() {
            val supermarket = Supermarket(id = 1, name = "TechStore", chainId = 1)
            supermarket.addProduct(productId = 3, initialStock = 100)
            supermarketRepository.addSupermarket(supermarket)

            val registerSale = RegisterSaleUseCase(supermarketRepository, productRepository, 1)
            val getProductRevenue = GetProductRevenueUseCase(supermarketRepository, 1)

            registerSale.execute(productId = 3, quantity = 10)  // 750
            registerSale.execute(productId = 3, quantity = 5)   // 375
            registerSale.execute(productId = 3, quantity = 8)   // 600

            val revenue = getProductRevenue.execute(productId = 3)
            assertEquals(1725.toBigDecimal(), revenue)
        }

        @Test
        fun `debe calcular ingresos totales con múltiples productos`() {
            val supermarket = Supermarket(id = 1, name = "TechStore", chainId = 1)
            supermarket.addProduct(productId = 1, initialStock = 100)
            supermarket.addProduct(productId = 2, initialStock = 100)
            supermarket.addProduct(productId = 3, initialStock = 100)
            supermarketRepository.addSupermarket(supermarket)

            val registerSale = RegisterSaleUseCase(supermarketRepository, productRepository, 1)
            val getTotalRevenue = GetTotalRevenueUseCase(supermarketRepository, 1)

            registerSale.execute(productId = 1, quantity = 2)   // 2000
            registerSale.execute(productId = 2, quantity = 10)  // 250
            registerSale.execute(productId = 3, quantity = 5)   // 375

            val totalRevenue = getTotalRevenue.execute()
            assertEquals(2625.toBigDecimal(), totalRevenue)
        }

        @Test
        fun `debe retornar cero para producto sin ventas`() {
            val supermarket = Supermarket(id = 1, name = "TechStore", chainId = 1)
            supermarket.addProduct(productId = 1, initialStock = 100)
            supermarketRepository.addSupermarket(supermarket)

            val getProductRevenue = GetProductRevenueUseCase(supermarketRepository, 1)
            val revenue = getProductRevenue.execute(productId = 1)

            assertEquals(BigDecimal.ZERO, revenue)
        }

        @Test
        fun `debe acumular correctamente ingresos con diferentes cantidades y precios`() {
            val supermarket = Supermarket(id = 1, name = "TechStore", chainId = 1)
            supermarket.addProduct(productId = 1, initialStock = 10)   // 1000
            supermarket.addProduct(productId = 4, initialStock = 10)   // 300
            supermarket.addProduct(productId = 5, initialStock = 50)   // 10
            supermarketRepository.addSupermarket(supermarket)

            val registerSale = RegisterSaleUseCase(supermarketRepository, productRepository, 1)

            registerSale.execute(productId = 1, quantity = 1)   // 1000
            registerSale.execute(productId = 4, quantity = 2)   // 600
            registerSale.execute(productId = 5, quantity = 10)  // 100

            val getTotalRevenue = GetTotalRevenueUseCase(supermarketRepository, 1)
            val totalRevenue = getTotalRevenue.execute()

            assertEquals(1700.toBigDecimal(), totalRevenue)
        }
    }

    @Nested
    inner class OpenHoursIntegrationTests {

        @Test
        fun `debe crear supermercado con horarios de atención`() {
            val hours = OpenHours(
                daysOpen = setOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY),
                openTime = LocalTime.of(9, 0),
                closeTime = LocalTime.of(18, 0)
            )

            val supermarket = Supermarket(
                id = 1,
                name = "TechStore",
                chainId = 1,
                hours = hours
            )
            supermarketRepository.addSupermarket(supermarket)

            assertNotNull(supermarket.hours)
            assertEquals(3, supermarket.hours!!.daysOpen.size)
        }

        @Test
        fun `debe validar correctamente si está abierto en día y hora específicos`() {
            val hours = OpenHours(
                daysOpen = setOf(DayOfWeek.MONDAY, DayOfWeek.FRIDAY),
                openTime = LocalTime.of(8, 0),
                closeTime = LocalTime.of(20, 0)
            )

            assertTrue(hours.isOpen(DayOfWeek.MONDAY, LocalTime.of(10, 0)))
            assertTrue(hours.isOpen(DayOfWeek.FRIDAY, LocalTime.of(19, 30)))
            assertFalse(hours.isOpen(DayOfWeek.MONDAY, LocalTime.of(7, 0)))
            assertFalse(hours.isOpen(DayOfWeek.SUNDAY, LocalTime.of(12, 0)))
        }

        @Test
        fun `debe permitir supermercado sin horarios definidos`() {
            val supermarket = Supermarket(
                id = 1,
                name = "TechStore 24/7",
                chainId = 1,
                hours = null
            )
            supermarketRepository.addSupermarket(supermarket)

            assertNull(supermarket.hours)
        }
    }

    @Nested
    inner class ComplexBusinessScenariosTests {

        @Test
        fun `debe manejar flujo completo de operaciones diarias`() {
            val supermarket = Supermarket(id = 1, name = "TechStore", chainId = 1)
            supermarket.addProduct(productId = 1, initialStock = 5)
            supermarket.addProduct(productId = 2, initialStock = 50)
            supermarket.addProduct(productId = 3, initialStock = 30)
            supermarketRepository.addSupermarket(supermarket)

            val registerSale = RegisterSaleUseCase(supermarketRepository, productRepository, 1)

            // Ventas de la mañana
            registerSale.execute(productId = 2, quantity = 10)
            registerSale.execute(productId = 3, quantity = 5)

            // Reposición de stock al mediodía
            supermarket.increaseStock(productId = 1, quantity = 10)

            // Ventas de la tarde
            registerSale.execute(productId = 1, quantity = 8)
            registerSale.execute(productId = 2, quantity = 15)

            val getTotalRevenue = GetTotalRevenueUseCase(supermarketRepository, 1)
            val totalRevenue = getTotalRevenue.execute()

            // (10*25) + (5*75) + (8*1000) + (15*25) = 250 + 375 + 8000 + 375 = 9000
            assertEquals(9000.toBigDecimal(), totalRevenue)
            assertEquals(4, supermarket.getSales().size)
        }

        @Test
        fun `debe calcular ROI de reposición de stock`() {
            val supermarket = Supermarket(id = 1, name = "TechStore", chainId = 1)
            supermarket.addProduct(productId = 4, initialStock = 10)
            supermarketRepository.addSupermarket(supermarket)

            val registerSale = RegisterSaleUseCase(supermarketRepository, productRepository, 1)
            val getTotalRevenue = GetTotalRevenueUseCase(supermarketRepository, 1)

            // Ventas con stock inicial
            registerSale.execute(productId = 4, quantity = 10)
            val revenueBeforeRestock = getTotalRevenue.execute()

            // Reponer y vender más
            supermarket.increaseStock(productId = 4, quantity = 20)
            registerSale.execute(productId = 4, quantity = 20)
            val revenueAfterRestock = getTotalRevenue.execute()

            assertEquals(3000.toBigDecimal(), revenueBeforeRestock)
            assertEquals(9000.toBigDecimal(), revenueAfterRestock)
        }

        @Test
        fun `debe gestionar promociones implícitas con diferentes precios históricos`() {
            val supermarket = Supermarket(id = 1, name = "TechStore", chainId = 1)
            supermarket.addProduct(productId = 5, initialStock = 100)
            supermarketRepository.addSupermarket(supermarket)

            val registerSale = RegisterSaleUseCase(supermarketRepository, productRepository, 1)

            // Varias ventas del mismo producto
            registerSale.execute(productId = 5, quantity = 10)
            registerSale.execute(productId = 5, quantity = 15)
            registerSale.execute(productId = 5, quantity = 20)

            val sales = supermarket.getSales()

            // Todas las ventas deben tener el mismo precio unitario
            assertTrue(sales.all { it.unitPrice == 10.toBigDecimal() })
            assertEquals(45, sales.sumOf { it.quantity })
        }

        @Test
        fun `debe prevenir inconsistencias en el inventario`() {
            val supermarket = Supermarket(id = 1, name = "TechStore", chainId = 1)
            supermarket.addProduct(productId = 2, initialStock = 20)
            supermarketRepository.addSupermarket(supermarket)

            val registerSale = RegisterSaleUseCase(supermarketRepository, productRepository, 1)

            registerSale.execute(productId = 2, quantity = 10)
            val stockAfterSale = supermarket.getProducts().first().currentStock()

            assertEquals(10, stockAfterSale)
            assertEquals(10, supermarket.getSoldQuantity(productId = 2))
        }
    }
}

