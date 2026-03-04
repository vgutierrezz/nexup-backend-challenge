package main.kotlin.core.domain.models

import java.math.BigDecimal
import main.kotlin.core.domain.exception.InvalidPriceException
import main.kotlin.core.domain.exception.InvalidProductNameException

class Product(
    val id: Long,
    val name: String,
    val price: BigDecimal
) {
    //Validaciones
    init {
        if (name.isBlank()) {
            throw InvalidProductNameException()
        }

        //Permito price=0 porque puede haber productos gratuitos/promociones
        if (price < BigDecimal.ZERO) {
            throw InvalidPriceException()
        }
    }
}