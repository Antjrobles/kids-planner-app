/**
 * ActividadRepository - Repositorio de datos para actividades extraescolares
 * Gestiono las operaciones CRUD de actividades vinculadas a familias e hijos.
 * Uso try/catch simple como enseña el temario de PMDM.
 */
package com.antjrobles.kidsplanner.modulos.actividad

import android.util.Log
import com.antjrobles.kidsplanner.database.ClienteSupabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class ActividadRepository {

    /**
     * crearActividad - Creo una nueva actividad en la base de datos
     * Devuelvo la actividad creada o null si hay error.
     */
    suspend fun crearActividad(
        tokenAcceso: String,
        familiaId: Int,
        hijoId: Int,
        nombre: String,
        descripcion: String? = null,
        lugar: String? = null,
        diaSemana: String? = null,
        horaInicio: String? = null,
        horaFin: String? = null,
        nombreProfesor: String? = null
    ): Actividad? = withContext(Dispatchers.IO) {
        try {
            val url = URL(ClienteSupabase.urlRest("activities"))
            val conexion = url.openConnection() as HttpURLConnection
            conexion.requestMethod = "POST"
            ClienteSupabase.configurarHeadersPost(conexion, tokenAcceso)
            conexion.doOutput = true

            // Creo el JSON con los datos
            val json = JSONObject()
            json.put("title", nombre)
            json.put("family_id", familiaId)
            json.put("child_id", hijoId)
            if (!descripcion.isNullOrBlank()) json.put("description", descripcion)
            if (!lugar.isNullOrBlank()) json.put("place", lugar)
            if (!diaSemana.isNullOrBlank()) json.put("weekday", diaSemana)
            if (!horaInicio.isNullOrBlank()) json.put("start_time", horaInicio)
            if (!horaFin.isNullOrBlank()) json.put("end_time", horaFin)
            if (!nombreProfesor.isNullOrBlank()) json.put("teacher_name", nombreProfesor)

            // Envío la petición
            val writer = conexion.outputStream.bufferedWriter()
            writer.write(json.toString())
            writer.close()

            // Leo la respuesta
            if (conexion.responseCode == 201) {
                val respuesta = conexion.inputStream.bufferedReader().readText()
                val arrayJson = JSONArray(respuesta)
                val actividadJson = arrayJson.getJSONObject(0)

                Actividad(
                    id = actividadJson.getInt("id"),
                    nombre = actividadJson.getString("title"),
                    familiaId = actividadJson.optInt("family_id"),
                    hijoId = actividadJson.optInt("child_id"),
                    descripcion = if (actividadJson.isNull("description")) null else actividadJson.getString("description"),
                    lugar = if (actividadJson.isNull("place")) null else actividadJson.getString("place"),
                    diaSemana = if (actividadJson.isNull("weekday")) null else actividadJson.getString("weekday"),
                    horaInicio = if (actividadJson.isNull("start_time")) null else actividadJson.getString("start_time"),
                    horaFin = if (actividadJson.isNull("end_time")) null else actividadJson.getString("end_time"),
                    nombreProfesor = if (actividadJson.isNull("teacher_name")) null else actividadJson.getString("teacher_name")
                )
            } else {
                Log.e("ActividadRepository", "Error al crear actividad: código ${conexion.responseCode}")
                null
            }
        } catch (e: Exception) {
            Log.e("ActividadRepository", "Error de conexión al crear actividad: ${e.message}", e)
            null
        }
    }

    /**
     * consultarActividades - Cargo la lista de actividades de una familia
     * Devuelvo lista vacía si hay error.
     */
    suspend fun consultarActividades(
        tokenAcceso: String,
        familiaId: Int
    ): List<Actividad> = withContext(Dispatchers.IO) {
        try {
            val urlCompleta = ClienteSupabase.urlRest("activities?family_id=eq.$familiaId")
            val url = URL(urlCompleta)
            val conexion = url.openConnection() as HttpURLConnection
            conexion.requestMethod = "GET"
            ClienteSupabase.configurarHeadersRest(conexion, tokenAcceso)

            if (conexion.responseCode == 200) {
                val respuesta = conexion.inputStream.bufferedReader().readText()
                val arrayJson = JSONArray(respuesta)

                val listaActividades = mutableListOf<Actividad>()
                for (i in 0 until arrayJson.length()) {
                    val actividadJson = arrayJson.getJSONObject(i)

                    val actividad = Actividad(
                        id = actividadJson.getInt("id"),
                        nombre = actividadJson.getString("title"),
                        familiaId = actividadJson.optInt("family_id"),
                        hijoId = actividadJson.optInt("child_id"),
                        descripcion = if (actividadJson.isNull("description")) null else actividadJson.getString("description"),
                        lugar = if (actividadJson.isNull("place")) null else actividadJson.getString("place"),
                        diaSemana = if (actividadJson.isNull("weekday")) null else actividadJson.getString("weekday"),
                        horaInicio = if (actividadJson.isNull("start_time")) null else actividadJson.getString("start_time"),
                        horaFin = if (actividadJson.isNull("end_time")) null else actividadJson.getString("end_time"),
                        nombreProfesor = if (actividadJson.isNull("teacher_name")) null else actividadJson.getString("teacher_name")
                    )

                    listaActividades.add(actividad)
                }

                listaActividades
            } else {
                Log.e("ActividadRepository", "Error al consultar actividades: código ${conexion.responseCode}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("ActividadRepository", "Error de conexión al consultar actividades: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * actualizarActividad - Actualizo los datos de una actividad existente
     * Devuelvo true si fue exitoso, false si hubo error.
     */
    suspend fun actualizarActividad(
        tokenAcceso: String,
        actividadId: Int,
        hijoId: Int,
        nombre: String,
        descripcion: String?,
        lugar: String?,
        diaSemana: String?,
        horaInicio: String?,
        horaFin: String?,
        nombreProfesor: String?
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val urlCompleta = ClienteSupabase.urlRest("activities?id=eq.$actividadId")
            val url = URL(urlCompleta)
            val conexion = url.openConnection() as HttpURLConnection
            conexion.requestMethod = "PATCH"
            ClienteSupabase.configurarHeadersPatch(conexion, tokenAcceso)
            conexion.doOutput = true

            // Creo el JSON con los datos actualizados
            val json = JSONObject()
            json.put("title", nombre)
            json.put("child_id", hijoId)
            if (!descripcion.isNullOrBlank()) json.put("description", descripcion)
            if (!lugar.isNullOrBlank()) json.put("place", lugar)
            if (!diaSemana.isNullOrBlank()) json.put("weekday", diaSemana)
            if (!horaInicio.isNullOrBlank()) json.put("start_time", horaInicio)
            if (!horaFin.isNullOrBlank()) json.put("end_time", horaFin)
            if (!nombreProfesor.isNullOrBlank()) json.put("teacher_name", nombreProfesor)

            // Envío la petición
            val writer = conexion.outputStream.bufferedWriter()
            writer.write(json.toString())
            writer.close()

            val codigo = conexion.responseCode
            if (codigo == 200 || codigo == 204) {
                true
            } else {
                Log.e("ActividadRepository", "Error al actualizar actividad: código $codigo")
                false
            }
        } catch (e: Exception) {
            Log.e("ActividadRepository", "Error de conexión al actualizar actividad: ${e.message}", e)
            false
        }
    }

    /**
     * eliminarActividad - Elimino una actividad de la base de datos
     * Devuelvo true si fue exitoso, false si hubo error.
     */
    suspend fun eliminarActividad(
        tokenAcceso: String,
        actividadId: Int
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val urlCompleta = ClienteSupabase.urlRest("activities?id=eq.$actividadId")
            val url = URL(urlCompleta)
            val conexion = url.openConnection() as HttpURLConnection
            conexion.requestMethod = "DELETE"
            ClienteSupabase.configurarHeadersDelete(conexion, tokenAcceso)

            val codigo = conexion.responseCode
            if (codigo == 200 || codigo == 204) {
                true
            } else {
                Log.e("ActividadRepository", "Error al eliminar actividad: código $codigo")
                false
            }
        } catch (e: Exception) {
            Log.e("ActividadRepository", "Error de conexión al eliminar actividad: ${e.message}", e)
            false
        }
    }
}
