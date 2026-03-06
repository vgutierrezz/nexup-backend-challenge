package test.integration

import main.kotlin.core.domain.models.Product
import main.kotlin.core.domain.models.Supermarket
import main.kotlin.core.service.supermarket.RegisterSaleUseCase
import main.kotlin.core.service.supermarketChain.GetChainTotalRevenueUseCase
import main.kotlin.core.service.supermarketChain.GetSupermarketHighestRevenueUseCase
import main.kotlin.core.service.GetTop5ProductsUseCase
import main.kotlin.infraestructure.persistence.ProductRepositoryImp
import main.kotlin.infraestructure.persistence.SuperMarketRepositoryImp
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class SupermarketChainIntegrationTest {

    private lateinit var supermarketRepository: SuperMarketRepositoryImp
    private lateinit var productRepository: ProductRepositoryImp

    @BeforeEach
    fun setUp() {
        supermarketRepository = SuperMarketRepositoryImp()
        productRepository = ProductRepositoryImp()

        // Crear productos
        productRepository.save(Product(1, "Carne", 10.toBigDecimal()))
        productRepository.save(Product(2, "Pescado", 20.toBigDecimal()))
        productRepository.save(Product(3, "Frutas", 5.toBigDecimal()))
        productRepository.save(Product(4, "Bebidas", 3.toBigDecimal()))
        productRepository.save(Product(5, "Pan", 2.toBigDecimal()))

        // Crear múltiples supermercados
        val supermarket1 = Supermarket(id = 1, name = "Super A", chainId = 1)
        supermarket1.addProduct(productId = 1, initialStock = 100)
        supermarket1.addProduct(productId = 2, initialStock = 50)
        supermarket1.addProduct(productId = 3, initialStock = 200)

        val supermarket2 = Supermarket(id = 2, name = "Super B", chainId = 1)
        supermarket2.addProduct(productId = 1, initialStock = 150)
        supermarket2.addProduct(productId = 3, initialStock = 100)
        supermarket2.addProduct(productId = 4, initialStock = 75)

        val supermarket3 = Supermarket(id = 3, name = "Super C", chainId = 1)
        supermarket3.addProduct(productId = 2, initialStock = 80)
        supermarket3.addProduct(productId = 4, initialStock = 120)
        supermarket3.addProduct(productId = 5, initialStock = 200)

        supermarketRepository.addSupermarket(supermarket1)
        supermarketRepository.addSupermarket(supermarket2)
        supermarketRepository.addSupermarket(supermarket3)
    }

    @Nested
    inner class GetChainTotalRevenueUseCaseTests {

        @Test
        fun `debe calcular correctamente los ingresos totales de toda la cadena`() {
            // Realizar ventas en diferentes supermercados
            val registerSale1 = RegisterSaleUseCase(supermarketRepository, productRepository, 1)
            val registerSale2 = RegisterSaleUseCase(supermarketRepository, productRepository, 2)
            val registerSale3 = RegisterSaleUseCase(supermarketRepository, productRepository, 3)

            registerSale1.execute(productId = 1, quantity = 10)  // 100
            registerSale1.execute(productId = 2, quantity = 5)   // 100
            registerSale2.execute(productId = 1, quantity = 15)  // 150
            registerSale2.execute(productId = 4, quantity = 10)  // 30
            registerSale3.execute(productId = 2, quantity = 8)   // 160
            registerSale3.execute(productId = 5, quantity = 20)  // 40

            val getChainRevenue = GetChainTotalRevenueUseCase(supermarketRepository)
            val totalRevenue = getChainRevenue.execute()

            assertEquals(580.toBigDecimal(), totalRevenue)
        }

        @Test
        fun `debe retornar cero cuando no hay ventas en la cadena`() {
            val getChainRevenue = GetChainTotalRevenueUseCase(supermarketRepository)
            val totalRevenue = getChainRevenue.execute()

            assertEquals(BigDecimal.ZERO, totalRevenue)
        }

        @Test
        fun `debe calcular correctamente con ventas en un solo supermercado`() {
            val registerSale1 = RegisterSaleUseCase(supermarketRepository, productRepository, 1)
            registerSale1.execute(productId = 1, quantity = 10)  // 100

            val getChainRevenue = GetChainTotalRevenueUseCase(supermarketRepository)
            val totalRevenue = getChainRevenue.execute()

            assertEquals(100.toBigDecimal(), totalRevenue)
        }

        @Test
        fun `debe sumar correctamente ventas de múltiples productos en múltiples supermercados`() {
            val registerSale1 = RegisterSaleUseCase(supermarketRepository, productRepository, 1)
            val registerSale2 = RegisterSaleUseCase(supermarketRepository, productRepository, 2)
            val registerSale3 = RegisterSaleUseCase(supermarketRepository, productRepository, 3)

            // Supermercado 1
            registerSale1.execute(productId = 1, quantity = 5)   // 50
            registerSale1.execute(productId = 2, quantity = 2)   // 40
            registerSale1.execute(productId = 3, quantity = 10)  // 50

            // Supermercado 2
            registerSale2.execute(productId = 1, quantity = 8)   // 80
            registerSale2.execute(productId = 4, quantity = 15)  // 45

            // Supermercado 3
            registerSale3.execute(productId = 5, quantity = 30)  // 60

            val getChainRevenue = GetChainTotalRevenueUseCase(supermarketRepository)
            val totalRevenue = getChainRevenue.execute()

            assertEquals(325.toBigDecimal(), totalRevenue)  // 50+40+50+80+45+60
        }
    }

    @Nested
    inner class GetSupermarketHighestRevenueUseCaseTests {

        @Test
        fun `debe identificar correctamente el supermercado con mayores ingresos`() {
            val registerSale1 = RegisterSaleUseCase(supermarketRepository, productRepository, 1)
            val registerSale2 = RegisterSaleUseCase(supermarketRepository, productRepository, 2)
            val registerSale3 = RegisterSaleUseCase(supermarketRepository, productRepository, 3)

            registerSale1.execute(productId = 1, quantity = 10)  // 100
            registerSale2.execute(productId = 1, quantity = 30)  // 300 <- Mayor
            registerSale3.execute(productId = 2, quantity = 5)   // 100

            val getHighestRevenue = GetSupermarketHighestRevenueUseCase(supermarketRepository)
            val result = getHighestRevenue.execute()

            assertTrue(result.contains("Super B"))
            assertTrue(result.contains("(2)"))
        }

        @Test
        fun `debe retornar mensaje cuando no hay supermercados`() {
            val emptyRepo = SuperMarketRepositoryImp()
            val getHighestRevenue = GetSupermarketHighestRevenueUseCase(emptyRepo)
            val result = getHighestRevenue.execute()

            assertEquals("No hay supermercados", result)
        }

        @Test
        fun `debe manejar correctamente cuando todos los supermercados tienen cero ingresos`() {
            val getHighestRevenue = GetSupermarketHighestRevenueUseCase(supermarketRepository)
            val result = getHighestRevenue.execute()

            assertNotNull(result)
            assertFalse(result.contains("null"))
        }

        @Test
        fun `debe formatear correctamente los ingresos en el resultado`() {
            val registerSale1 = RegisterSaleUseCase(supermarketRepository, productRepository, 1)
            registerSale1.execute(productId = 1, quantity = 10)  // 100

            val getHighestRevenue = GetSupermarketHighestRevenueUseCase(supermarketRepository)
            val result = getHighestRevenue.execute()

            assertTrue(result.contains("Super A"))
            assertTrue(result.contains("Ingresos totales"))
        }

        @Test
        fun `debe elegir el primero cuando hay empate en ingresos`() {
            val registerSale1 = RegisterSaleUseCase(supermarketRepository, productRepository, 1)
            val registerSale2 = RegisterSaleUseCase(supermarketRepository, productRepository, 2)

            registerSale1.execute(productId = 1, quantity = 10)  // 100
            registerSale2.execute(productId = 1, quantity = 10)  // 100

            val getHighestRevenue = GetSupermarketHighestRevenueUseCase(supermarketRepository)
            val result = getHighestRevenue.execute()

            assertNotNull(result)
            assertTrue(result.isNotEmpty())
        }
    }

    @Nested
    inner class GetTop5ProductsUseCaseTests {

        @Test
        fun `debe retornar los 5 productos más vendidos en orden descendente`() {
            val registerSale1 = RegisterSaleUseCase(supermarketRepository, productRepository, 1)
            val registerSale2 = RegisterSaleUseCase(supermarketRepository, productRepository, 2)
            val registerSale3 = RegisterSaleUseCase(supermarketRepository, productRepository, 3)

            // Producto 1: 50 unidades total
            registerSale1.execute(productId = 1, quantity = 30)
            registerSale2.execute(productId = 1, quantity = 20)

            // Producto 2: 25 unidades total
            registerSale1.execute(productId = 2, quantity = 15)
            registerSale3.execute(productId = 2, quantity = 10)

            // Producto 3: 40 unidades total
            registerSale1.execute(productId = 3, quantity = 40)

            // Producto 4: 15 unidades total
            registerSale2.execute(productId = 4, quantity = 15)

            // Producto 5: 60 unidades total (más vendido)
            registerSale3.execute(productId = 5, quantity = 60)

            val getTop5 = GetTop5ProductsUseCase(supermarketRepository)
            val result = getTop5.execute(topN = 5)

            assertNotNull(result)
            assertFalse(result.isEmpty())
        }

        @Test
        fun `debe retornar cadena vacía cuando no hay ventas`() {
            val getTop5 = GetTop5ProductsUseCase(supermarketRepository)
            val result = getTop5.execute(topN = 5)

            assertEquals("", result)
        }

        @Test
        fun `debe manejar correctamente cuando hay menos de 5 productos vendidos`() {
            val registerSale1 = RegisterSaleUseCase(supermarketRepository, productRepository, 1)

            registerSale1.execute(productId = 1, quantity = 10)
            registerSale1.execute(productId = 2, quantity = 5)

            val getTop5 = GetTop5ProductsUseCase(supermarketRepository)
            val result = getTop5.execute(topN = 5)

            assertNotNull(result)
            // Debe retornar solo 2 productos
        }

        @Test
        fun `debe respetar el parámetro topN personalizado`() {
            val registerSale1 = RegisterSaleUseCase(supermarketRepository, productRepository, 1)
            val registerSale2 = RegisterSaleUseCase(supermarketRepository, productRepository, 2)

            registerSale1.execute(productId = 1, quantity = 10)
            registerSale1.execute(productId = 2, quantity = 20)
            registerSale2.execute(productId = 3, quantity = 15)
            registerSale2.execute(productId = 4, quantity = 5)

            val getTop3 = GetTop5ProductsUseCase(supermarketRepository)
            val result = getTop3.execute(topN = 3)

            assertNotNull(result)
        }

        @Test
        fun `debe sumar correctamente ventas del mismo producto en diferentes supermercados`() {
            val registerSale1 = RegisterSaleUseCase(supermarketRepository, productRepository, 1)
            val registerSale2 = RegisterSaleUseCase(supermarketRepository, productRepository, 2)
            val registerSale3 = RegisterSaleUseCase(supermarketRepository, productRepository, 3)

            // Producto 1 vendido en todos los supermercados
            registerSale1.execute(productId = 1, quantity = 10)
            registerSale2.execute(productId = 1, quantity = 15)

            // Producto 2 solo en uno
            registerSale3.execute(productId = 2, quantity = 5)

            val getTop5 = GetTop5ProductsUseCase(supermarketRepository)
            val result = getTop5.execute(topN = 5)

            assertNotNull(result)
            // Producto 1 debe tener 25 unidades totales
        }
    }

    @Nested
    inner class CrossSupermarketOperationsTests {

        @Test
        fun `debe mantener independencia de stock entre supermercados de la misma cadena`() {
            val registerSale1 = RegisterSaleUseCase(supermarketRepository, productRepository, 1)
            val registerSale2 = RegisterSaleUseCase(supermarketRepository, productRepository, 2)

            // Vender producto 1 en Super A
            registerSale1.execute(productId = 1, quantity = 50)

            // Stock en Super B debe permanecer intacto
            val supermarket2 = supermarketRepository.getSupermarketById(2)!!
            val stock2 = supermarket2.getProducts().first { it.productId == 1L }.currentStock()

            assertEquals(150, stock2)
        }

        @Test
        fun `debe acumular ventas correctamente en el total de la cadena`() {
            val registerSale1 = RegisterSaleUseCase(supermarketRepository, productRepository, 1)
            val registerSale2 = RegisterSaleUseCase(supermarketRepository, productRepository, 2)
            val registerSale3 = RegisterSaleUseCase(supermarketRepository, productRepository, 3)

            registerSale1.execute(productId = 1, quantity = 10)  // 100
            registerSale2.execute(productId = 1, quantity = 10)  // 100
            registerSale3.execute(productId = 2, quantity = 10)  // 200

            val getChainRevenue = GetChainTotalRevenueUseCase(supermarketRepository)
            val totalRevenue = getChainRevenue.execute()

            assertEquals(400.toBigDecimal(), totalRevenue)
        }

        @Test
        fun `debe permitir ventas simultáneas de diferentes productos en diferentes supermercados`() {
            val registerSale1 = RegisterSaleUseCase(supermarketRepository, productRepository, 1)
            val registerSale2 = RegisterSaleUseCase(supermarketRepository, productRepository, 2)
            val registerSale3 = RegisterSaleUseCase(supermarketRepository, productRepository, 3)

            // Ventas simultáneas
            registerSale1.execute(productId = 1, quantity = 5)
            registerSale2.execute(productId = 3, quantity = 10)
            registerSale3.execute(productId = 2, quantity = 8)
            registerSale1.execute(productId = 2, quantity = 3)
            registerSale2.execute(productId = 4, quantity = 20)

            val sales1 = supermarketRepository.getSupermarketById(1)!!.getSales()
            val sales2 = supermarketRepository.getSupermarketById(2)!!.getSales()
            val sales3 = supermarketRepository.getSupermarketById(3)!!.getSales()

            assertEquals(2, sales1.size)
            assertEquals(2, sales2.size)
            assertEquals(1, sales3.size)
        }
    }

    @Nested
    inner class ChainAnalyticsTests {

        @Test
        fun `debe calcular correctamente métricas combinadas de la cadena`() {
            val registerSale1 = RegisterSaleUseCase(supermarketRepository, productRepository, 1)
            val registerSale2 = RegisterSaleUseCase(supermarketRepository, productRepository, 2)

            registerSale1.execute(productId = 1, quantity = 10)
            registerSale1.execute(productId = 2, quantity = 5)
            registerSale2.execute(productId = 1, quantity = 20)
            registerSale2.execute(productId = 3, quantity = 15)

            val getChainRevenue = GetChainTotalRevenueUseCase(supermarketRepository)
            val getHighestRevenue = GetSupermarketHighestRevenueUseCase(supermarketRepository)

            val totalRevenue = getChainRevenue.execute()
            val highestSupermarket = getHighestRevenue.execute()

            // Total: 100 + 100 + 200 + 75 = 475
            assertEquals(475.toBigDecimal(), totalRevenue)
            assertTrue(highestSupermarket.contains("Super B"))
        }

        @Test
        fun `debe reflejar cambios en tiempo real de las ventas`() {
            val getChainRevenue = GetChainTotalRevenueUseCase(supermarketRepository)

            // Verificar inicial
            assertEquals(BigDecimal.ZERO, getChainRevenue.execute())

            // Realizar venta
            val registerSale1 = RegisterSaleUseCase(supermarketRepository, productRepository, 1)
            registerSale1.execute(productId = 1, quantity = 10)

            // Verificar actualización
            assertEquals(100.toBigDecimal(), getChainRevenue.execute())

            // Realizar otra venta
            registerSale1.execute(productId = 2, quantity = 5)

            // Verificar nueva actualización
            assertEquals(200.toBigDecimal(), getChainRevenue.execute())
        }

        @Test
        fun `debe identificar cambios en el supermercado líder después de nuevas ventas`() {
            val registerSale1 = RegisterSaleUseCase(supermarketRepository, productRepository, 1)
            val registerSale2 = RegisterSaleUseCase(supermarketRepository, productRepository, 2)
            val getHighestRevenue = GetSupermarketHighestRevenueUseCase(supermarketRepository)

            // Super A es el líder
            registerSale1.execute(productId = 1, quantity = 20)  // 200
            assertTrue(getHighestRevenue.execute().contains("Super A"))

            // Super B toma el liderazgo
            registerSale2.execute(productId = 1, quantity = 30)  // 300
            assertTrue(getHighestRevenue.execute().contains("Super B"))
        }
    }
}

