/**
 * AuthRepository - Repository para operaciones de autenticación
 * Centralizo la lógica de login con Supabase Auth API.
 * Uso try/catch simple como enseña el temario de PMDM.
 */
package com.antjrobles.kidsplanner.repositorios

import android.util.Log
import com.antjrobles.kidsplanner.database.ClienteSupabase
import com.antjrobles.kidsplanner.modulos.familia.RegistrarUsuario
import com.antjrobles.kidsplanner.modulos.familia.ResultadoRegistrarUsuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Repository que gestiona la autenticación de usuarios
 */
class AuthRepository {

    private val registrarUsuario = RegistrarUsuario()

    /**
     * iniciarSesion - Inicio sesión con email y contraseña usando Supabase Auth
     * Devuelvo DatosLogin con token y userId si tiene éxito, null si hay error.
     */
    suspend fun iniciarSesion(
        correo: String,
        contrasena: String
    ): DatosLogin? = withContext(Dispatchers.IO) {
        try {
            val url = URL(ClienteSupabase.urlAuth("token?grant_type=password"))
            val conexion = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                ClienteSupabase.configurarHeadersAuth(this)
                doOutput = true
            }

            conexion.outputStream.bufferedWriter().use {
                it.write(JSONObject().apply {
                    put("email", correo)
                    put("password", contrasena)
                    put("grant_type", "password")
                }.toString())
            }

            val respuesta = JSONObject(conexion.inputStream.bufferedReader().readText())
            val token = respuesta.optString("access_token", "")
            val userId = respuesta.optJSONObject("user")?.optString("id", "") ?: ""

            if (token.isNotEmpty() && userId.isNotEmpty()) {
                DatosLogin(token, userId)
            } else {
                Log.e("AuthRepository", "Credenciales inválidas")
                null
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error de conexión al iniciar sesión: ${e.message}", e)
            null
        }
    }

    /**
     * registrar - Registro de nuevo usuario con email y contraseña usando Supabase Auth
     * La respuesta de signup ya incluye el token, por lo que no es necesario un login posterior.
     * Devuelvo DatosLogin con token y userId si tiene éxito, null si hay error.
     */
    suspend fun registrar(
        correo: String,
        contrasena: String
    ): DatosLogin? = withContext(Dispatchers.IO) {
        try {
            val url = URL(ClienteSupabase.urlAuth("signup"))
            val conexion = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                ClienteSupabase.configurarHeadersAuth(this)
                doOutput = true
            }

            conexion.outputStream.bufferedWriter().use {
                it.write(JSONObject().apply {
                    put("email", correo)
                    put("password", contrasena)
                    put("data", JSONObject().apply {
                        put("full_name", correo)
                    })
                }.toString())
            }

            val codigo = conexion.responseCode
            Log.d("AuthRepository", "Código respuesta signup: $codigo")

            if (codigo in 200..299) {
                val respuestaTexto = conexion.inputStream.bufferedReader().readText()
                Log.d("AuthRepository", "Respuesta signup: $respuestaTexto")
                val respuesta = JSONObject(respuestaTexto)

                // El token y el ID están en el nivel principal
                val token = respuesta.optString("access_token", "")
                val userId = respuesta.optString("id", "")

                if (token.isNotEmpty() && userId.isNotEmpty()) {
                    // Aseguro que el usuario también existe en public.users con full_name correcto
                    val registro = registrarUsuario.asegurar(token, userId, correo)

                    if (registro is ResultadoRegistrarUsuario.Error) {
                        Log.e("AuthRepository", "Error al crear usuario: ${registro.mensaje}")
                        null
                    } else {
                        // ¡Éxito! Devuelvo el token y el ID directamente.
                        DatosLogin(token, userId)
                    }
                } else {
                    // Si no hay token (email confirmation activado), hago login directo
                    Log.d("AuthRepository", "No hay token en signup, haciendo login automático...")
                    iniciarSesion(correo, contrasena)
                }
            } else {
                val errorTexto = try {
                    val textoError = conexion.errorStream?.bufferedReader()?.readText() ?: "Sin detalles"
                    Log.e("AuthRepository", "Error signup código $codigo: $textoError")
                    textoError
                } catch (e: Exception) {
                    Log.e("AuthRepository", "No se pudo leer errorStream: ${e.message}")
                    "Error al leer respuesta"
                }
                Log.e("AuthRepository", "Error al registrar: código $codigo: $errorTexto")
                null
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error de conexión al registrar: ${e.message}", e)
            null
        }
    }
}

/**
 * Modelo de datos que representa la respuesta exitosa de login
 */
data class DatosLogin(
    val token: String,
    val userId: String
)
