import main.kotlin.core.domain.models.Product
import main.kotlin.core.domain.models.Supermarket
import main.kotlin.core.domain.models.SupermarketChain
import main.kotlin.core.service.supermarket.RegisterSaleUseCase
import main.kotlin.core.service.supermarket.GetTotalRevenueUseCase
import main.kotlin.core.service.supermarket.GetProductRevenueUseCase
import main.kotlin.core.service.supermarket.GetProductSoldQuantityUseCase
import main.kotlin.core.service.supermarketChain.GetChainTotalRevenueUseCase
import main.kotlin.core.service.supermarketChain.GetSupermarketHighestRevenueUseCase
import main.kotlin.core.service.GetTop5ProductsUseCase
import main.kotlin.infraestructure.persistence.SuperMarketRepositoryImp
import main.kotlin.infraestructure.persistence.ProductRepositoryImp
import main.kotlin.infraestructure.delivery.GlobalExceptionHandler
import java.math.BigDecimal

fun main() {
    // ============ INICIALIZAR REPOSITORIOS ============
    val supermarketRepository = SuperMarketRepositoryImp()
    val productRepository = ProductRepositoryImp()

    GlobalExceptionHandler.handle {
        // ============ CREAR PRODUCTOS ============
        println("📦 Creando productos...")
        val product1 = Product(id = 1, name = "Carne", price = 10.toBigDecimal())
        val product2 = Product(id = 2, name = "Leche", price = 2.toBigDecimal())
        val product3 = Product(id = 3, name = "Pan", price = 5.toBigDecimal())
        val product4 = Product(id = 4, name = "Queso", price = 3.toBigDecimal())
        val product5 = Product(id = 5, name = "Huevos", price = 1.toBigDecimal())

        productRepository.save(product1)
        productRepository.save(product2)
        productRepository.save(product3)
        productRepository.save(product4)
        productRepository.save(product5)
        println("✅ ${productRepository.getAll().size} productos creados\n")

        // ============ CREAR CADENA Y SUPERMERCADOS ============
        println("🏢 Creando supermercados y cadena...")
        val chainId = 1L
        val supermarketChain = SupermarketChain(id = chainId, name = "Cadena Nexup")

        // Supermercado 1: Super A
        val supermarket1 = Supermarket(id = 1, name = "Super A", chainId = chainId)
        supermarket1.addProduct(productId = 1, initialStock = 50)  // Carne
        supermarket1.addProduct(productId = 2, initialStock = 100) // Leche
        supermarket1.addProduct(productId = 3, initialStock = 80)  // Pan
        supermarketRepository.addSupermarket(supermarket1)

        // Supermercado 2: Super B
        val supermarket2 = Supermarket(id = 2, name = "Super B", chainId = chainId)
        supermarket2.addProduct(productId = 1, initialStock = 60)  // Carne
        supermarket2.addProduct(productId = 4, initialStock = 120) // Queso
        supermarket2.addProduct(productId = 5, initialStock = 200) // Huevos
        supermarketRepository.addSupermarket(supermarket2)

        // Supermercado 3: Super C
        val supermarket3 = Supermarket(id = 3, name = "Super C", chainId = chainId)
        supermarket3.addProduct(productId = 2, initialStock = 150) // Leche
        supermarket3.addProduct(productId = 3, initialStock = 90)  // Pan
        supermarket3.addProduct(productId = 4, initialStock = 75)  // Queso
        supermarketRepository.addSupermarket(supermarket3)

        println("✅ ${supermarketRepository.getAllSupermarkets().size} supermercados creados\n")

        // ============ REALIZAR VENTAS ============
        println("🛒 Realizando ventas...\n")

        // Ventas en Super A
        println("📍 Super A:")
        val registerSaleA = RegisterSaleUseCase(supermarketRepository, productRepository, supermarketId = 1)
        println("  • Venta 1: 5x Carne = \$${registerSaleA.execute(productId = 1, quantity = 5)}")
        println("  • Venta 2: 10x Leche = \$${registerSaleA.execute(productId = 2, quantity = 10)}")
        println("  • Venta 3: 8x Pan = \$${registerSaleA.execute(productId = 3, quantity = 8)}\n")

        // Ventas en Super B
        println("📍 Super B:")
        val registerSaleB = RegisterSaleUseCase(supermarketRepository, productRepository, supermarketId = 2)
        println("  • Venta 1: 3x Carne = \$${registerSaleB.execute(productId = 1, quantity = 3)}")
        println("  • Venta 2: 20x Queso = \$${registerSaleB.execute(productId = 4, quantity = 20)}")
        println("  • Venta 3: 50x Huevos = \$${registerSaleB.execute(productId = 5, quantity = 50)}\n")

        // Ventas en Super C
        println("📍 Super C:")
        val registerSaleC = RegisterSaleUseCase(supermarketRepository, productRepository, supermarketId = 3)
        println("  • Venta 1: 15x Leche = \$${registerSaleC.execute(productId = 2, quantity = 15)}")
        println("  • Venta 2: 12x Pan = \$${registerSaleC.execute(productId = 3, quantity = 12)}")
        println("  • Venta 3: 5x Queso = \$${registerSaleC.execute(productId = 4, quantity = 5)}\n")

        // ============ CONSULTAS POR SUPERMERCADO ============
        println("=".repeat(60))
        println("📊 REPORTES POR SUPERMERCADO")
        println("=".repeat(60) + "\n")

        for (supermarketId in 1L..3L) {
            println("📍 Supermercado ID: $supermarketId")

            val totalRevenueUC = GetTotalRevenueUseCase(supermarketRepository, supermarketId)
            println("  • Ingresos Totales: \$${totalRevenueUC.execute()}")

            val productRevenueUC = GetProductRevenueUseCase(supermarketRepository, supermarketId)
            println("  • Ingresos por Carne (ID=1): \$${productRevenueUC.execute(productId = 1)}")

            val quantitySoldUC = GetProductSoldQuantityUseCase(supermarketRepository, supermarketId)
            println("  • Cantidad vendida Leche (ID=2): ${quantitySoldUC.execute(productId = 2)} unidades\n")
        }

        // ============ CONSULTAS POR CADENA ============
        println("=".repeat(60))
        println("🏢 REPORTES DE CADENA - Nexup")
        println("=".repeat(60) + "\n")

        val chainRevenueUC = GetChainTotalRevenueUseCase(supermarketRepository)
        val chainTotalRevenue = chainRevenueUC.execute()
        println("💰 Ingresos Totales de la Cadena: \$$chainTotalRevenue\n")

        val highestRevenueUC = GetSupermarketHighestRevenueUseCase(supermarketRepository)
        println("🏆 Supermercado con Mayor Ingreso:")
        println("   ${highestRevenueUC.execute()}\n")

        val top5UC = GetTop5ProductsUseCase(supermarketRepository)
        println("⭐ Top 5 Productos más Vendidos:")
        val top5Result = top5UC.execute(topN = 5)
        if (top5Result.isNotEmpty()) {
            println("   $top5Result")
        } else {
            println("   (No hay datos disponibles)")
        }

        println("\n" + "=".repeat(60))
        println("✅ Fin de la demostración")
        println("=".repeat(60))
    }
}