/**
 * ClienteSupabase - Cliente HTTP para comunicación con Supabase
 * Gestiono la configuración única para todas las peticiones a la API de Supabase.
 * Uso la ANON_KEY pública y paso el token del usuario para cumplir permisos RLS.
 */
package com.antjrobles.kidsplanner.database

import com.antjrobles.kidsplanner.BuildConfig
import java.net.HttpURLConnection

/**
 * ClienteSupabase - Configuración ÚNICA y CORRECTA para Supabase.
 * - Usa la ANON_KEY (la pública) para todas las peticiones.
 * - Pasa el TOKEN del usuario cuando es necesario para cumplir los permisos (RLS).
 * - No hay ninguna puta referencia al esquema "kids".
 */
object ClienteSupabase {

    // Usamos las claves del local.properties que has actualizado.
    private val baseUrl = BuildConfig.SUPABASE_URL.trimEnd('/')
    private val anonKey = BuildConfig.SUPABASE_ANON_KEY

    fun urlAuth(endpoint: String) = "$baseUrl/auth/v1/$endpoint"
    fun urlRest(tabla: String) = "$baseUrl/rest/v1/$tabla"

    /**
     * Para peticiones PÚBLICAS (login, registro) donde aún no hay token.
     */
    fun configurarHeadersAuth(conexion: HttpURLConnection) {
        conexion.setRequestProperty("apikey", anonKey)
        conexion.setRequestProperty("Content-Type", "application/json")
    }

    /**
     * Para peticiones GET autenticadas (leer datos).
     */
    fun configurarHeadersRest(conexion: HttpURLConnection, token: String) {
        conexion.setRequestProperty("apikey", anonKey)
        conexion.setRequestProperty("Authorization", "Bearer $token")
    }

    /**
     * Para peticiones POST autenticadas (crear datos).
     */
    fun configurarHeadersPost(conexion: HttpURLConnection, token: String) {
        conexion.setRequestProperty("apikey", anonKey)
        conexion.setRequestProperty("Authorization", "Bearer $token")
        conexion.setRequestProperty("Content-Type", "application/json")
        conexion.setRequestProperty("Prefer", "return=representation")
    }

    /**
     * Para peticiones PATCH autenticadas (actualizar datos).
     */
    fun configurarHeadersPatch(conexion: HttpURLConnection, token: String) {
        conexion.setRequestProperty("apikey", anonKey)
        conexion.setRequestProperty("Authorization", "Bearer $token")
        conexion.setRequestProperty("Content-Type", "application/json")
    }

    /**
     * Para peticiones DELETE autenticadas (borrar datos).
     */
    fun configurarHeadersDelete(conexion: HttpURLConnection, token: String) {
        conexion.setRequestProperty("apikey", anonKey)
        conexion.setRequestProperty("Authorization", "Bearer $token")
    }
}
