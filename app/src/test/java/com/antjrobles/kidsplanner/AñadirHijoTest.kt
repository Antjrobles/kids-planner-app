package com.antjrobles.kidsplanner

import com.antjrobles.kidsplanner.modulos.hijo.Hijo
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * AñadirHijoTest - Tests para verificar modelo Hijo
 * Pruebo que el data class Hijo guarda correctamente los datos.
 */
class AñadirHijoTest {

    /**
     * Verifico que Hijo guarda correctamente todos los datos.
     */
    @Test
    fun hijo_conserva_datos_correctamente() {
        // Preparo un hijo de ejemplo con todos los campos
        val hijo = Hijo(
            id = 5,
            nombre = "María",
            familiaId = 10,
            fechaNacimiento = "2015-05-20",
            cursoEscolar = "4º Primaria",
            alergias = "Polen"
        )

        // Verifico que los datos se guardaron correctamente
        println("ID recibido: ${hijo.id}")
        println("Nombre recibido: ${hijo.nombre}")
        println("FamiliaId recibido: ${hijo.familiaId}")
        println("Fecha nacimiento: ${hijo.fechaNacimiento}")
        println("Curso: ${hijo.cursoEscolar}")
        println("Alergias: ${hijo.alergias}")

        assertEquals(5, hijo.id)
        assertEquals("María", hijo.nombre)
        assertEquals(10, hijo.familiaId)
        assertEquals("2015-05-20", hijo.fechaNacimiento)
        assertEquals("4º Primaria", hijo.cursoEscolar)
        assertEquals("Polen", hijo.alergias)
    }

    /**
     * Verifico que Hijo permite campos opcionales como null.
     */
    @Test
    fun hijo_permite_campos_opcionales_null() {
        // Preparo un hijo solo con campos obligatorios
        val hijo = Hijo(
            id = 3,
            nombre = "Juan",
            familiaId = 8
        )

        // Verifico que los campos opcionales son null
        assertEquals(null, hijo.fechaNacimiento)
        assertEquals(null, hijo.cursoEscolar)
        assertEquals(null, hijo.alergias)
    }
}
