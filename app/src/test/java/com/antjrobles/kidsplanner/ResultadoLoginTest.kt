package com.antjrobles.kidsplanner

import com.antjrobles.kidsplanner.repositorios.DatosLogin
import org.junit.Test
import org.junit.Assert.*

/**
 * ResultadoLoginTest - Tests para verificar modelo DatosLogin
 * Pruebo que el data class DatosLogin guarda correctamente token y userId.
 */
class ResultadoLoginTest {

    /**
     * Verifico que DatosLogin guarda correctamente el token y userId.
     */
    @Test
    fun datos_login_conserva_token_y_userid() {
        // Preparo datos de prueba
        val tokenEsperado = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test"
        val userIdEsperado = "uuid-prueba-123"

        // Creo un objeto DatosLogin con esos datos
        val datos = DatosLogin(tokenEsperado, userIdEsperado)

        // Verifico que lo que saco es lo mismo que metí
        println("Token recibido: ${datos.token}")
        println("UserId recibido: ${datos.userId}")

        assertEquals(tokenEsperado, datos.token)
        assertEquals(userIdEsperado, datos.userId)
    }

    /**
     * Verifico que DatosLogin acepta tokens largos reales.
     */
    @Test
    fun datos_login_acepta_tokens_largos() {
        // Preparo un token JWT largo realista
        val tokenLargo = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
        val userId = "550e8400-e29b-41d4-a716-446655440000"

        // Creo el objeto
        val datos = DatosLogin(tokenLargo, userId)

        // Verifico que se guardó sin problemas
        assertEquals(tokenLargo, datos.token)
        assertEquals(userId, datos.userId)
        assertTrue(datos.token.length > 100)
    }

    /**
     * Verifico que DatosLogin maneja UUIDs correctamente.
     */
    @Test
    fun datos_login_acepta_uuid_formato_correcto() {
        val token = "token123"
        val uuid = "a1b2c3d4-e5f6-1234-5678-9abcdef01234"

        val datos = DatosLogin(token, uuid)

        // Verifico que el UUID se guardó con el formato correcto
        assertTrue(datos.userId.contains("-"))
        assertEquals(36, datos.userId.length) // Los UUID tienen 36 caracteres
    }
}
