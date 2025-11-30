/**
 * Hijo - Modelo de datos de un hijo
 * Defino la estructura de datos para hijos con todos sus campos (nombre, fecha nacimiento,
 * curso escolar, alergias).
 */
package com.antjrobles.kidsplanner.modulos.hijo

/**
 * Representa un hijo asociado a una familia.
 */
data class Hijo(
    val id: Int,
    val nombre: String,
    val familiaId: Int?,
    val fechaNacimiento: String? = null,    // Fecha de Nacimiento (formato: YYYY-MM-DD)
    val cursoEscolar: String? = null,       // Curso: "3º Primaria")
    val alergias: String? = null            // Alergias
)
