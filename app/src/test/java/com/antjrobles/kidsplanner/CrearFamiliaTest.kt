package com.antjrobles.kidsplanner

import com.antjrobles.kidsplanner.modulos.familia.FamiliaCreada
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * CrearFamiliaTest - Tests para verificar modelo FamiliaCreada
 * Pruebo que el data class FamiliaCreada guarda correctamente los datos.
 */
class CrearFamiliaTest {

    /**
     * Verifico que FamiliaCreada guarda correctamente los datos.
     */
    @Test
    fun familia_conserva_datos_correctamente() {
        // Preparo una familia de ejemplo
        val familia = FamiliaCreada(id = 42, nombre = "Familia Robles - Cuesta - Ejemplo")

        // Verifico que los datos se guardaron correctamente
        println("ID recibido: ${familia.id}")
        println("Nombre recibido: ${familia.nombre}")

        assertEquals(42, familia.id)
        assertEquals("Familia Robles - Cuesta - Ejemplo", familia.nombre)
    }

    /**
     * Verifico que FamiliaCreada maneja nombres con caracteres especiales.
     */
    @Test
    fun familia_acepta_caracteres_especiales() {
        // Preparo familia con caracteres especiales
        val familia = FamiliaCreada(
            id = 99,
            nombre = "Familia García-López & Pérez (2024)"
        )

        // Verifico que se guardó sin problemas
        assertEquals(99, familia.id)
        assertEquals("Familia García-López & Pérez (2024)", familia.nombre)
    }
}
