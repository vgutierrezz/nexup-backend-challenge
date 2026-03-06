package main.kotlin.infraestructure.delivery

import main.kotlin.core.domain.exception.DomainException
import main.kotlin.infraestructure.exception.SupermarketNotFoundException

object GlobalExceptionHandler {

    fun <T> handle(action: () -> T): Result<T> =
        try {
            Result.success(action())
        } catch (e: DomainException) {
            logError("DOMAIN_ERROR", e)
            Result.failure(e)
        } catch (e: SupermarketNotFoundException) {
            logError("APPLICATION_ERROR", e)
            Result.failure(e)
        } catch (e: Exception) {
            logError("INTERNAL_ERROR", e)
            Result.failure(e)
        }

    private fun logError(type: String, e: Throwable) {
        val timestamp = java.time.LocalDateTime.now()
        println("[$timestamp] [$type]: ${e.message ?: "Sin mensaje"}")
    }
}