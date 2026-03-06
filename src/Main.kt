import main.kotlin.core.service.supermarket.RegisterSaleUseCase
import main.kotlin.infraestructure.persistence.SuperMarketRepositoryImp
import main.kotlin.core.service.analytics.GetChainAnalyticsUseCase
import main.kotlin.infraestructure.delivery.ExceptionHandler

fun main() {
    val repository = SuperMarketRepositoryImp()
    val analytics = GetChainAnalyticsUseCase(repository, chainId = 10L)
    val saleUC = RegisterSaleUseCase(repository, 1L)

    GlobalExceptionHandler.handle {
        // Realizamos una venta de prueba
        saleUC.execute(productId = 1L, quantity = 2)

        // Imprimimos el reporte final
        println(analytics.executeAllReports())
    }
}