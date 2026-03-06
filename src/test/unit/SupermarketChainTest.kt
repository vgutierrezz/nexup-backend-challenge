package main.kotlin.core.domain.models

import main.kotlin.core.domain.exception.InvalidNameException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class SupermarketChainTest {

    @Test
    fun `crear cadena de supermercados con datos válidos`() {
        val chain = SupermarketChain(
            id = 1L,
            name = "Cadena Principal"
        )

        assertEquals(1L, chain.id)
        assertEquals("Cadena Principal", chain.name)
    }

    @Test
    fun `crear cadena con nombre vacío lanza InvalidNameException`() {
        assertThrows(InvalidNameException::class.java) {
            SupermarketChain(
                id = 1L,
                name = ""
            )
        }
    }

    @Test
    fun `crear cadena con nombre en blanco lanza InvalidNameException`() {
        assertThrows(InvalidNameException::class.java) {
            SupermarketChain(
                id = 1L,
                name = "   "
            )
        }
    }

    @Test
    fun `crear cadena con nombre válido es exitoso`() {
        val chain = SupermarketChain(
            id = 1L,
            name = "Cadena de Supermercados XYZ"
        )

        assertEquals("Cadena de Supermercados XYZ", chain.name)
    }

    @Test
    fun `crear cadena con nombre de un carácter`() {
        val chain = SupermarketChain(
            id = 1L,
            name = "A"
        )

        assertEquals("A", chain.name)
    }
}

