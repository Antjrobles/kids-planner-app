/**
 * FamiliaRepository - Repositorio de datos para familias
 * Gestiono las operaciones CRUD de familias en Supabase.
 * Consulto familias del usuario y creo nuevas familias con validación de permisos.
 * Uso try/catch simple como enseña el temario de PMDM.
 */
package com.antjrobles.kidsplanner.modulos.familia

import android.util.Log
import com.antjrobles.kidsplanner.database.ClienteSupabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class FamiliaRepository {

    /**
     * consultarFamilias - Cargo las familias del usuario
     * Devuelvo lista vacía si hay error.
     */
    suspend fun consultarFamilias(
        tokenAcceso: String,
        usuarioId: String
    ): List<Familia> = withContext(Dispatchers.IO) {
        try {
            val url = URL("${ClienteSupabase.urlRest("families")}?created_by=eq.$usuarioId")
            val conexion = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                ClienteSupabase.configurarHeadersRest(this, tokenAcceso)
            }

            if (conexion.responseCode == 200) {
                val familias = mutableListOf<Familia>()
                val jsonArray = JSONArray(conexion.inputStream.bufferedReader().readText())

                for (i in 0 until jsonArray.length()) {
                    val familiaObj = jsonArray.getJSONObject(i)
                    familias.add(
                        Familia(
                            id = familiaObj.getInt("id"),
                            nombre = familiaObj.getString("name"),
                            creadoPor = familiaObj.getString("created_by")
                        )
                    )
                }
                familias
            } else {
                Log.e("FamiliaRepository", "Error al cargar familias: código ${conexion.responseCode}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("FamiliaRepository", "Error de conexión al cargar familias: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * crearFamilia - Creo una nueva familia en Supabase
     * Devuelvo FamiliaCreada con id y nombre si tiene éxito, null si hay error.
     */
    suspend fun crearFamilia(
        tokenAcceso: String,
        nombreFamilia: String,
        usuarioId: String
    ): FamiliaCreada? = withContext(Dispatchers.IO) {
        try {
            val url = URL(ClienteSupabase.urlRest("families"))
            val conexion = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                ClienteSupabase.configurarHeadersPost(this, tokenAcceso)
                doOutput = true
            }

            conexion.outputStream.bufferedWriter().use {
                it.write(JSONObject().apply {
                    put("name", nombreFamilia)
                    put("created_by", usuarioId)
                }.toString())
            }

            if (conexion.responseCode in 200..299) {
                val json = JSONArray(conexion.inputStream.bufferedReader().readText())
                val primera = json.getJSONObject(0)
                val familiaId = primera.getInt("id")
                FamiliaCreada(familiaId, primera.getString("name"))
            } else {
                Log.e("FamiliaRepository", "Error al crear familia: código ${conexion.responseCode}")
                null
            }
        } catch (e: Exception) {
            Log.e("FamiliaRepository", "Error de conexión al crear familia: ${e.message}", e)
            null
        }
    }
}

data class FamiliaCreada(val id: Int, val nombre: String)
