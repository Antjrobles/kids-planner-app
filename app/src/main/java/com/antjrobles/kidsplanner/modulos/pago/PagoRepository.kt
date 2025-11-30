/**
 * PagoRepository - Repositorio de datos para pagos de actividades
 * Gestiono los pagos asociados a actividades con fechas y estados.
 * Permito marcar pagos como pagados con fecha y método de pago.
 * Uso try/catch simple como enseña el temario de PMDM.
 */
package com.antjrobles.kidsplanner.modulos.pago

import android.util.Log
import com.antjrobles.kidsplanner.database.ClienteSupabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class PagoRepository {

    /**
     * crearPago - Creo un nuevo pago en Supabase
     * Devuelvo true si se crea exitosamente, false si hay error.
     */
    suspend fun crearPago(
        tokenAcceso: String,
        familiaId: Int,
        actividadId: Int,
        monto: Double,
        fechaVencimiento: String,
        notas: String? = null
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL(ClienteSupabase.urlRest("payments"))
            val conexion = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                ClienteSupabase.configurarHeadersPost(this, tokenAcceso)
                doOutput = true
            }

            conexion.outputStream.bufferedWriter().use {
                it.write(JSONObject().apply {
                    put("family_id", familiaId)
                    put("activity_id", actividadId)
                    put("amount", monto)
                    put("due_date", fechaVencimiento)
                    put("status", "pending")
                    if (!notas.isNullOrBlank()) put("notes", notas)
                }.toString())
            }

            if (conexion.responseCode == 201) {
                true
            } else {
                Log.e("PagoRepository", "Error al crear pago: código ${conexion.responseCode}")
                false
            }
        } catch (e: Exception) {
            Log.e("PagoRepository", "Error de conexión al crear pago: ${e.message}", e)
            false
        }
    }

    /**
     * consultarPagos - Cargo la lista de pagos de una actividad
     * Devuelvo lista vacía si hay error.
     */
    suspend fun consultarPagos(
        tokenAcceso: String,
        actividadId: Int
    ): List<Pago> = withContext(Dispatchers.IO) {
        try {
            val urlCompleta = ClienteSupabase.urlRest("payments?activity_id=eq.$actividadId")
            val url = URL(urlCompleta)
            val conexion = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                ClienteSupabase.configurarHeadersRest(this, tokenAcceso)
            }

            if (conexion.responseCode == 200) {
                val respuesta = conexion.inputStream.bufferedReader().readText()
                val arrayJson = JSONArray(respuesta)

                val listaPagos = mutableListOf<Pago>()
                for (i in 0 until arrayJson.length()) {
                    val pagoJson = arrayJson.getJSONObject(i)

                    val pago = Pago(
                        id = pagoJson.getInt("id"),
                        familiaId = pagoJson.getInt("family_id"),
                        actividadId = pagoJson.getInt("activity_id"),
                        monto = pagoJson.getDouble("amount"),
                        fechaVencimiento = pagoJson.getString("due_date"),
                        estado = pagoJson.getString("status"),
                        fechaPago = if (pagoJson.isNull("paid_date")) null else pagoJson.getString("paid_date"),
                        metodoPago = if (pagoJson.isNull("payment_method")) null else pagoJson.getString("payment_method"),
                        notas = if (pagoJson.isNull("notes")) null else pagoJson.getString("notes")
                    )

                    listaPagos.add(pago)
                }

                listaPagos
            } else {
                Log.e("PagoRepository", "Error al consultar pagos: código ${conexion.responseCode}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("PagoRepository", "Error de conexión al consultar pagos: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * marcarComoPagado - Marco un pago como pagado con fecha y método
     * Devuelvo true si se actualiza exitosamente, false si hay error.
     */
    suspend fun marcarComoPagado(
        tokenAcceso: String,
        pagoId: Int,
        fechaPago: String,
        metodoPago: String
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val urlCompleta = ClienteSupabase.urlRest("payments?id=eq.$pagoId")
            val url = URL(urlCompleta)
            val conexion = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "PATCH"
                ClienteSupabase.configurarHeadersPatch(this, tokenAcceso)
                doOutput = true
            }

            conexion.outputStream.bufferedWriter().use {
                it.write(JSONObject().apply {
                    put("status", "paid")
                    put("paid_date", fechaPago)
                    put("payment_method", metodoPago)
                }.toString())
            }

            if (conexion.responseCode in 200..299) {
                true
            } else {
                Log.e("PagoRepository", "Error al actualizar pago: código ${conexion.responseCode}")
                false
            }
        } catch (e: Exception) {
            Log.e("PagoRepository", "Error de conexión al actualizar pago: ${e.message}", e)
            false
        }
    }

    /**
     * eliminarPago - Elimino un pago de Supabase
     * Devuelvo true si se elimina exitosamente, false si hay error.
     */
    suspend fun eliminarPago(
        tokenAcceso: String,
        pagoId: Int
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val urlCompleta = ClienteSupabase.urlRest("payments?id=eq.$pagoId")
            val url = URL(urlCompleta)
            val conexion = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "DELETE"
                ClienteSupabase.configurarHeadersDelete(this, tokenAcceso)
            }

            val codigo = conexion.responseCode
            if (codigo == 200 || codigo == 204) {
                true
            } else {
                Log.e("PagoRepository", "Error al eliminar pago: código $codigo")
                false
            }
        } catch (e: Exception) {
            Log.e("PagoRepository", "Error de conexión al eliminar pago: ${e.message}", e)
            false
        }
    }
}
