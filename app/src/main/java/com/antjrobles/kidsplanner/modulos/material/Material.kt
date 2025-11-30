/**
 * Material - Modelo de datos para materiales de actividades
 * Defino la estructura de un material necesario para una actividad extraescolar.
 */
package com.antjrobles.kidsplanner.modulos.material

/**
 * Representa un material necesario para una actividad extraescolar
 */
data class Material(
    val id: Int,
    val nombre: String,
    val actividadId: Int?,
    val descripcion: String? = null,
    val cantidad: Int? = 1,
    val preparado: Boolean = false,  // Si ya está listo/empaquetado
    val notas: String? = null
)
