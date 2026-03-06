package main.kotlin.core.domain.models

import main.kotlin.core.domain.exception.InvalidNameException

class SupermarketChain (
    val id: Long,
    val name: String
) {
    //Validaciones
    init {
        if (name.isBlank()) {
            throw InvalidNameException()
        }
    }
}