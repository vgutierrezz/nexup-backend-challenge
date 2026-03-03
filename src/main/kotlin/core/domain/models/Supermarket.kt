package com.nexup.challenge.domain.models

import main.kotlin.core.domain.models.Sale

data class Supermarket(
    val id: Long,
    val name: String,
    val products: List<Product> = listOf(),
    val stock: MutableMap<Long, Int> = mutableMapOf(),
    val sales: MutableList<Sale> = mutableListOf(),
    val hours: OpenHours? = null //Relaciòn 1 a 1
    //Mantiene limpia la clase Supermerket
) {

}