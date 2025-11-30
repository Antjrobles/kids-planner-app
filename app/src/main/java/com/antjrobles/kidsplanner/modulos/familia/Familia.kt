/**
 * Familia - Modelo de datos para familias
 * Defino la estructura básica de una familia: id, nombre y usuario creador.
 */
package com.antjrobles.kidsplanner.modulos.familia

data class Familia(
    val id: Int,
    val nombre: String,
    val creadoPor: String
)
