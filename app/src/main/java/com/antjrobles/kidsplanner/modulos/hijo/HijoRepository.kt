/**
 * HijoRepository - Repositorio de datos para hijos
 * Gestiono las operaciones CRUD de hijos vinculados a familias.
 * Uso try/catch simple como enseña el temario de PMDM.
 */
package com.antjrobles.kidsplanner.modulos.hijo

import android.util.Log
import com.antjrobles.kidsplanner.database.ClienteSupabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class HijoRepository {

    /**
     * crearHijo - Creo un nuevo hijo en la base de datos
     * Devuelvo el hijo creado o null si hay error.
     */
    suspend fun crearHijo(
        tokenAcceso: String,
        familiaId: Int,
        nombre: String,
        fechaNacimiento: String? = null,
        cursoEscolar: String? = null,
        alergias: String? = null
    ): Hijo? = withContext(Dispatchers.IO) {
        try {
            val url = URL(ClienteSupabase.urlRest("children"))
            val conexion = url.openConnection() as HttpURLConnection
            conexion.requestMethod = "POST"
            ClienteSupabase.configurarHeadersPost(conexion, tokenAcceso)
            conexion.doOutput = true

            // Creo el JSON con los datos del hijo
            val json = JSONObject()
            json.put("name", nombre)
            json.put("family_id", familiaId)
            if (fechaNacimiento != null) json.put("birthdate", fechaNacimiento)
            if (cursoEscolar != null) json.put("grade", cursoEscolar)
            if (alergias != null) json.put("allergies", alergias)

            // Envío la petición
            val writer = conexion.outputStream.bufferedWriter()
            writer.write(json.toString())
            writer.close()

            // Leo la respuesta
            if (conexion.responseCode == 201) {
                val respuesta = conexion.inputStream.bufferedReader().readText()
                val arrayJson = JSONArray(respuesta)
                val hijoCreado = arrayJson.getJSONObject(0)

                Hijo(
                    id = hijoCreado.getInt("id"),
                    nombre = hijoCreado.getString("name"),
                    familiaId = hijoCreado.getInt("family_id"),
                    fechaNacimiento = if (hijoCreado.has("birthdate")) hijoCreado.getString("birthdate") else null,
                    cursoEscolar = if (hijoCreado.has("grade")) hijoCreado.getString("grade") else null,
                    alergias = if (hijoCreado.has("allergies")) hijoCreado.getString("allergies") else null
                )
            } else {
                Log.e("HijoRepository", "Error al crear hijo: código ${conexion.responseCode}")
                null
            }
        } catch (e: Exception) {
            Log.e("HijoRepository", "Error de conexión al crear hijo: ${e.message}", e)
            null
        }
    }

    /**
     * consultarHijos - Cargo la lista de hijos de una familia
     * Devuelvo lista vacía si hay error.
     */
    suspend fun consultarHijos(
        tokenAcceso: String,
        familiaId: Int
    ): List<Hijo> = withContext(Dispatchers.IO) {
        try {
            val urlCompleta = ClienteSupabase.urlRest("children?family_id=eq.$familiaId")
            val url = URL(urlCompleta)
            val conexion = url.openConnection() as HttpURLConnection
            conexion.requestMethod = "GET"
            ClienteSupabase.configurarHeadersRest(conexion, tokenAcceso)

            if (conexion.responseCode == 200) {
                val respuesta = conexion.inputStream.bufferedReader().readText()
                val arrayJson = JSONArray(respuesta)

                val listaHijos = mutableListOf<Hijo>()
                for (i in 0 until arrayJson.length()) {
                    val hijoJson = arrayJson.getJSONObject(i)

                    val hijo = Hijo(
                        id = hijoJson.getInt("id"),
                        nombre = hijoJson.getString("name"),
                        familiaId = hijoJson.getInt("family_id"),
                        fechaNacimiento = if (hijoJson.has("birthdate")) hijoJson.getString("birthdate") else null,
                        cursoEscolar = if (hijoJson.has("grade")) hijoJson.getString("grade") else null,
                        alergias = if (hijoJson.has("allergies")) hijoJson.getString("allergies") else null
                    )

                    listaHijos.add(hijo)
                }

                listaHijos
            } else {
                Log.e("HijoRepository", "Error al consultar hijos: código ${conexion.responseCode}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("HijoRepository", "Error de conexión al consultar hijos: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * actualizarHijo - Actualizo los datos de un hijo existente
     * Devuelvo true si fue exitoso, false si hubo error.
     */
    suspend fun actualizarHijo(
        tokenAcceso: String,
        hijoId: Int,
        nombre: String,
        fechaNacimiento: String?,
        cursoEscolar: String?,
        alergias: String?
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val urlCompleta = ClienteSupabase.urlRest("children?id=eq.$hijoId")
            val url = URL(urlCompleta)
            val conexion = url.openConnection() as HttpURLConnection
            conexion.requestMethod = "PATCH"
            ClienteSupabase.configurarHeadersPatch(conexion, tokenAcceso)
            conexion.doOutput = true

            // Creo el JSON con los datos actualizados
            val json = JSONObject()
            json.put("name", nombre)
            if (fechaNacimiento != null) json.put("birthdate", fechaNacimiento)
            if (cursoEscolar != null) json.put("grade", cursoEscolar)
            if (alergias != null) json.put("allergies", alergias)

            // Envío la petición
            val writer = conexion.outputStream.bufferedWriter()
            writer.write(json.toString())
            writer.close()

            if (conexion.responseCode == 200) {
                true
            } else {
                Log.e("HijoRepository", "Error al actualizar hijo: código ${conexion.responseCode}")
                false
            }
        } catch (e: Exception) {
            Log.e("HijoRepository", "Error de conexión al actualizar hijo: ${e.message}", e)
            false
        }
    }

    /**
     * eliminarHijo - Elimino un hijo de la base de datos
     * Devuelvo true si fue exitoso, false si hubo error.
     */
    suspend fun eliminarHijo(
        tokenAcceso: String,
        hijoId: Int
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val urlCompleta = ClienteSupabase.urlRest("children?id=eq.$hijoId")
            val url = URL(urlCompleta)
            val conexion = url.openConnection() as HttpURLConnection
            conexion.requestMethod = "DELETE"
            ClienteSupabase.configurarHeadersDelete(conexion, tokenAcceso)

            val codigo = conexion.responseCode
            if (codigo == 200 || codigo == 204) {
                true
            } else {
                Log.e("HijoRepository", "Error al eliminar hijo: código $codigo")
                false
            }
        } catch (e: Exception) {
            Log.e("HijoRepository", "Error de conexión al eliminar hijo: ${e.message}", e)
            false
        }
    }
}
