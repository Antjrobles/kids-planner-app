/**
 * PantallaRegistro - Pantalla para crear nueva cuenta
 * Muestro formulario de registro (email + contraseña + confirmar contraseña) con validaciones
 * y conexión a Supabase para crear nuevos usuarios.
 */
package com.antjrobles.kidsplanner.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.antjrobles.kidsplanner.componentes.PrimaryButton
import com.antjrobles.kidsplanner.tema.KidsPlannerColors
// Resultado eliminado - ahora uso nullable pattern
import com.antjrobles.kidsplanner.repositorios.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

/**
 * Pantalla de Registro
 * Pantalla para que los usuarios creen una nueva cuenta con email y contraseña
 */
@Composable
fun PantallaRegistro(
    alVolverAtras: () -> Unit = {},
    registroCorrecto: (String, String, String) -> Unit = { _, _, _ -> }
) {
    val context = LocalContext.current

    // Estados para los campos del formulario
    val correo = remember { mutableStateOf("") }
    val contrasena = remember { mutableStateOf("") }
    val confirmarContrasena = remember { mutableStateOf("") }

    // Estado para indicador de carga
    val isLoading = remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize(), color = KidsPlannerColors.Background) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Ícono de registro
                Text(
                    text = "📝",
                    fontSize = 64.sp,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Título
                Text(
                    text = "Crear Cuenta",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = KidsPlannerColors.TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Descripción
                Text(
                    text = "Regístrate para empezar a usar Kids Planner",
                    fontSize = 12.sp,
                    color = KidsPlannerColors.TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Campo: Email
                OutlinedTextField(
                    value = correo.value,
                    onValueChange = { correo.value = it },
                    label = { Text("Email") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = KidsPlannerColors.Primary,
                        unfocusedBorderColor = KidsPlannerColors.TextSecondary,
                        focusedLabelColor = KidsPlannerColors.Primary,
                        unfocusedLabelColor = KidsPlannerColors.TextSecondary
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo: Contraseña
                OutlinedTextField(
                    value = contrasena.value,
                    onValueChange = { contrasena.value = it },
                    label = { Text("Contraseña") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = KidsPlannerColors.Primary,
                        unfocusedBorderColor = KidsPlannerColors.TextSecondary,
                        focusedLabelColor = KidsPlannerColors.Primary,
                        unfocusedLabelColor = KidsPlannerColors.TextSecondary
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo: Confirmar Contraseña
                OutlinedTextField(
                    value = confirmarContrasena.value,
                    onValueChange = { confirmarContrasena.value = it },
                    label = { Text("Confirmar Contraseña") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = KidsPlannerColors.Primary,
                        unfocusedBorderColor = KidsPlannerColors.TextSecondary,
                        focusedLabelColor = KidsPlannerColors.Primary,
                        unfocusedLabelColor = KidsPlannerColors.TextSecondary
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Botón: Registrarse
                PrimaryButton(
                    text = "Crear Cuenta",
                    onClick = {
                        if (!isLoading.value) {
                            val correoSinEspacios = correo.value.trim()
                            val contrasenaSinEspacios = contrasena.value.trim()
                            val confirmarSinEspacios = confirmarContrasena.value.trim()

                            // Validaciones
                            if (correoSinEspacios.isBlank() || contrasenaSinEspacios.isBlank() || confirmarSinEspacios.isBlank()) {
                                Toast.makeText(context, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                            } else if (!correoSinEspacios.contains("@")) {
                                Toast.makeText(context, "Por favor ingresa un email válido", Toast.LENGTH_SHORT).show()
                            } else if (contrasenaSinEspacios.length < 6) {
                                Toast.makeText(context, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                            } else if (contrasenaSinEspacios != confirmarSinEspacios) {
                                Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                            } else {
                                isLoading.value = true
                                CoroutineScope(Dispatchers.IO).launch {
                                    val authRepository = AuthRepository()
                                    val datosLogin = authRepository.registrar(correoSinEspacios, contrasenaSinEspacios)
                                    Log.d("PantallaRegistro", "Resultado registro: $datosLogin")

                                    withContext(Dispatchers.Main) {
                                        isLoading.value = false

                                        if (datosLogin != null) {
                                            Log.d("PantallaRegistro", "Registro exitoso")
                                            Toast.makeText(context, "Cuenta creada exitosamente", Toast.LENGTH_LONG).show()
                                            registroCorrecto(datosLogin.token, datosLogin.userId, correoSinEspacios)
                                        } else {
                                            Log.e("PantallaRegistro", "Error en registro")
                                            Toast.makeText(context, "Error al crear la cuenta", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Indicador de carga
                if (isLoading.value) {
                    CircularProgressIndicator(
                        color = KidsPlannerColors.Primary,
                        modifier = Modifier.padding(8.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Botón: Volver
                Button(
                    onClick = alVolverAtras,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(horizontal = 32.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = KidsPlannerColors.Primary
                    )
                ) {
                    Text(
                        text = "Volver",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
