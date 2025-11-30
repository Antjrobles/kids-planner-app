/**
 * Actividad - Modelo de datos de una actividad
 * Defino la estructura de datos para actividades extraescolares con información completa
 * (título, descripción, lugar, día, horarios, profesor).
 */
package com.antjrobles.kidsplanner.modulos.actividad

data class Actividad(
    val id: Int,
    val nombre: String,
    val familiaId: Int?,
    val hijoId: Int?,
    val descripcion: String? = null,
    val lugar: String? = null,
    val diaSemana: String? = null,
    val horaInicio: String? = null,
    val horaFin: String? = null,
    val nombreProfesor: String? = null
)
