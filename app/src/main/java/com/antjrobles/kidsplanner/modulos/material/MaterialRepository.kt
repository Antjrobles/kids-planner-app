/**
 * MaterialRepository - Repositorio de datos para materiales de actividades
 * Gestiono los materiales necesarios para cada actividad extraescolar.
 * Uso try/catch simple como enseña el temario de PMDM.
 */
package com.antjrobles.kidsplanner.modulos.material

import android.util.Log
import com.antjrobles.kidsplanner.database.ClienteSupabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class MaterialRepository {

    /**
     * consultarMateriales - Cargo la lista de materiales de una actividad
     * Devuelvo lista vacía si hay error.
     */
    suspend fun consultarMateriales(
        tokenAcceso: String,
        actividadId: Int
    ): List<Material> = withContext(Dispatchers.IO) {
        try {
            val url = URL("${ClienteSupabase.urlRest("materials")}?activity_id=eq.$actividadId")
            val conexion = url.openConnection() as HttpURLConnection
            conexion.requestMethod = "GET"
            ClienteSupabase.configurarHeadersRest(conexion, tokenAcceso)

            if (conexion.responseCode in 200..299) {
                val respuesta = conexion.inputStream.bufferedReader().readText()
                val json = JSONArray(respuesta)

                val materiales = mutableListOf<Material>()
                for (i in 0 until json.length()) {
                    val obj = json.getJSONObject(i)
                    materiales.add(
                        Material(
                            id = obj.getInt("id"),
                            nombre = obj.getString("name"),
                            actividadId = obj.optInt("activity_id").takeIf { it != 0 },
                            descripcion = obj.optString("description").takeIf { it.isNotBlank() },
                            cantidad = obj.optInt("quantity").takeIf { it != 0 } ?: 1,
                            preparado = obj.optBoolean("checked", false),
                            notas = obj.optString("notes").takeIf { it.isNotBlank() }
                        )
                    )
                }

                materiales
            } else {
                Log.e("MaterialRepository", "Error al consultar materiales: código ${conexion.responseCode}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("MaterialRepository", "Error de conexión al consultar materiales: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * crearMaterial - Creo un nuevo material para una actividad
     * Devuelvo el material creado o null si hay error.
     */
    suspend fun crearMaterial(
        tokenAcceso: String,
        actividadId: Int,
        nombre: String,
        cantidad: Int
    ): Material? = withContext(Dispatchers.IO) {
        try {
            val url = URL(ClienteSupabase.urlRest("materials"))
            val conexion = url.openConnection() as HttpURLConnection
            conexion.requestMethod = "POST"
            ClienteSupabase.configurarHeadersPost(conexion, tokenAcceso)
            conexion.doOutput = true

            // Creo el JSON con los datos
            val json = JSONObject()
            json.put("name", nombre)
            json.put("activity_id", actividadId)
            json.put("quantity", cantidad)
            json.put("checked", false)

            // Envío la petición
            val writer = conexion.outputStream.bufferedWriter()
            writer.write(json.toString())
            writer.close()

            // Leo la respuesta
            if (conexion.responseCode == 201) {
                val respuesta = conexion.inputStream.bufferedReader().readText()
                val arrayJson = JSONArray(respuesta)
                val obj = arrayJson.getJSONObject(0)

                Material(
                    id = obj.getInt("id"),
                    nombre = obj.getString("name"),
                    actividadId = obj.optInt("activity_id").takeIf { it != 0 },
                    descripcion = obj.optString("description").takeIf { it.isNotBlank() },
                    cantidad = obj.optInt("quantity").takeIf { it != 0 } ?: 1,
                    preparado = obj.optBoolean("checked", false),
                    notas = obj.optString("notes").takeIf { it.isNotBlank() }
                )
            } else {
                Log.e("MaterialRepository", "Error al crear material: código ${conexion.responseCode}")
                null
            }
        } catch (e: Exception) {
            Log.e("MaterialRepository", "Error de conexión al crear material: ${e.message}", e)
            null
        }
    }

    /**
     * actualizarEstado - Actualizo el estado preparado de un material
     * Devuelvo true si fue exitoso, false si hubo error.
     */
    suspend fun actualizarEstado(
        tokenAcceso: String,
        materialId: Int,
        preparado: Boolean
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL("${ClienteSupabase.urlRest("materials")}?id=eq.$materialId")
            val conexion = url.openConnection() as HttpURLConnection
            conexion.requestMethod = "PATCH"
            ClienteSupabase.configurarHeadersPatch(conexion, tokenAcceso)
            conexion.doOutput = true

            // Creo el JSON con el nuevo estado
            val json = JSONObject()
            json.put("checked", preparado)

            // Envío la petición
            val writer = conexion.outputStream.bufferedWriter()
            writer.write(json.toString())
            writer.close()

            if (conexion.responseCode in 200..299) {
                true
            } else {
                Log.e("MaterialRepository", "Error al actualizar material: código ${conexion.responseCode}")
                false
            }
        } catch (e: Exception) {
            Log.e("MaterialRepository", "Error de conexión al actualizar material: ${e.message}", e)
            false
        }
    }

    /**
     * eliminarMaterial - Elimino un material de la base de datos
     * Devuelvo true si fue exitoso, false si hubo error.
     */
    suspend fun eliminarMaterial(
        tokenAcceso: String,
        materialId: Int
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL("${ClienteSupabase.urlRest("materials")}?id=eq.$materialId")
            val conexion = url.openConnection() as HttpURLConnection
            conexion.requestMethod = "DELETE"
            ClienteSupabase.configurarHeadersDelete(conexion, tokenAcceso)

            val codigo = conexion.responseCode
            if (codigo in 200..299) {
                true
            } else {
                Log.e("MaterialRepository", "Error al eliminar material: código $codigo")
                false
            }
        } catch (e: Exception) {
            Log.e("MaterialRepository", "Error de conexión al eliminar material: ${e.message}", e)
            false
        }
    }
}
