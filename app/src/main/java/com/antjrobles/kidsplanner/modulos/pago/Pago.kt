/**
 * Pago - Modelo de datos para pagos de actividades
 * Defino la estructura de datos de pagos asociados a actividades.
 */
package com.antjrobles.kidsplanner.modulos.pago

/**
 * Modelo de datos que representa un pago de actividad
 */
data class Pago(
    val id: Int,
    val familiaId: Int,
    val actividadId: Int,
    val monto: Double,
    val fechaVencimiento: String,  // formato "YYYY-MM-DD"
    val estado: String,              // "pending", "paid", "overdue"
    val fechaPago: String? = null,   // formato "YYYY-MM-DD", nullable
    val metodoPago: String? = null,  // "efectivo", "tarjeta", "transferencia", nullable
    val notas: String? = null
)
