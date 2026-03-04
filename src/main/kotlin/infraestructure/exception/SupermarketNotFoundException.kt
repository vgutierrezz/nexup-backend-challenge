package main.kotlin.infraestructure.exception

class SupermarketNotFoundException(id: Long) :
    RuntimeException("Supermercado $id no encontrado")