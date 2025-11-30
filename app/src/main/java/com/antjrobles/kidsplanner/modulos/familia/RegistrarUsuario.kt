/**
 * RegistrarUsuario - Capa de datos para asegurar que el usuario existe en la BD
 * Verifico si el usuario autenticado existe en public.users y lo creo si no existe.
 * Necesario antes de crear familias por las restricciones de clave foránea.
 */
package com.antjrobles.kidsplanner.modulos.familia

import com.antjrobles.kidsplanner.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class RegistrarUsuario {
    suspend fun asegurar(tokenAcceso: String, usuarioId: String, correo: String?): ResultadoRegistrarUsuario = withContext(Dispatchers.IO) {
        try {
            val base = BuildConfig.SUPABASE_URL.trimEnd('/')
            val conexion = (URL("$base/rest/v1/users?id=eq.$usuarioId").openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("apikey", BuildConfig.SUPABASE_SERVICE_ROLE_KEY)
                setRequestProperty("Authorization", "Bearer ${BuildConfig.SUPABASE_SERVICE_ROLE_KEY}")
            }

            if (conexion.responseCode in 200..299) {
                val json = JSONArray(conexion.inputStream.bufferedReader().readText())
                if (json.length() > 0) {
                    // Usuario existe, verifico si tiene full_name
                    val usuario = json.getJSONObject(0)
                    val fullName = if (usuario.isNull("full_name")) null else usuario.getString("full_name")

                    if (fullName.isNullOrBlank()) {
                        // Si full_name está vacío o null, lo actualizo
                        actualizar(base, usuarioId, correo)
                    } else {
                        ResultadoRegistrarUsuario.Existe
                    }
                } else {
                    crear(base, usuarioId, correo)
                }
            } else {
                ResultadoRegistrarUsuario.Error("Error al comprobar")
            }
        } catch (ex: Exception) {
            return@withContext ResultadoRegistrarUsuario.Error("Error de conexión: ${ex.message}")
        }
    }

    private fun crear(base: String, usuarioId: String, correo: String?): ResultadoRegistrarUsuario {
        return try {
            val conexion = (URL("$base/rest/v1/users").openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("apikey", BuildConfig.SUPABASE_SERVICE_ROLE_KEY)
                setRequestProperty("Authorization", "Bearer ${BuildConfig.SUPABASE_SERVICE_ROLE_KEY}")
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Prefer", "return=minimal")
                doOutput = true
            }

            conexion.outputStream.bufferedWriter().use {
                it.write(JSONObject().apply {
                    put("id", usuarioId)
                    val emailFinal = if (correo.isNullOrBlank()) "sin-email@kidsplanner.local" else correo
                    val nombreFinal = if (correo.isNullOrBlank()) "Usuario Kids Planner" else correo
                    put("email", emailFinal)
                    put("full_name", nombreFinal)
                }.toString())
            }

            if (conexion.responseCode in 200..299) ResultadoRegistrarUsuario.Creado
            else ResultadoRegistrarUsuario.Error("Error al crear")
        } catch (ex: Exception) {
            ResultadoRegistrarUsuario.Error("Error de conexión")
        }
    }

    private fun actualizar(base: String, usuarioId: String, correo: String?): ResultadoRegistrarUsuario {
        return try {
            val conexion = (URL("$base/rest/v1/users?id=eq.$usuarioId").openConnection() as HttpURLConnection).apply {
                requestMethod = "PATCH"
                setRequestProperty("apikey", BuildConfig.SUPABASE_SERVICE_ROLE_KEY)
                setRequestProperty("Authorization", "Bearer ${BuildConfig.SUPABASE_SERVICE_ROLE_KEY}")
                setRequestProperty("Content-Type", "application/json")
                doOutput = true
            }

            conexion.outputStream.bufferedWriter().use {
                it.write(JSONObject().apply {
                    val emailFinal = if (correo.isNullOrBlank()) "sin-email@kidsplanner.local" else correo
                    val nombreFinal = if (correo.isNullOrBlank()) "Usuario Kids Planner" else correo
                    put("email", emailFinal)
                    put("full_name", nombreFinal)
                }.toString())
            }

            if (conexion.responseCode in 200..299) ResultadoRegistrarUsuario.Existe
            else ResultadoRegistrarUsuario.Error("Error al actualizar")
        } catch (ex: Exception) {
            ResultadoRegistrarUsuario.Error("Error de conexión")
        }
    }
}

sealed class ResultadoRegistrarUsuario {
    object Existe : ResultadoRegistrarUsuario()
    object Creado : ResultadoRegistrarUsuario()
    data class Error(val mensaje: String) : ResultadoRegistrarUsuario()
}
