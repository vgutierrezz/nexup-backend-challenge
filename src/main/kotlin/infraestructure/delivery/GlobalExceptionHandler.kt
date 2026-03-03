package main.kotlin.infraestructure.delivery

import main.kotlin.core.domain.exception.SupermarketException
import java.time.LocalDateTime
import javax.swing.Action

class GlobalExceptionHandler {
    fun <T> handle(action: () -> T): T? {
        return try {
            action()
        } catch (e: SupermarketException) {
            // Errores conocidos de lógica de negocio
            logError("BUSINESS_ERROR", e.message ?: "Error de negocio desconocido")
            null
        } catch (e: Exception) {
            // Errores inesperados (NullPointer, etc.)
            logError("INTERNAL_ERROR", "Ocurrió un error inesperado: ${e.message}")
            null
        }
    }

    private fun logError(type: String, message: String) {
        val timestamp = LocalDateTime.now()
        println("[$timestamp] [$type]: $message")
    }
}