/**
 * PantallaLogin - Pantalla de autenticación
 * Muestro formulario de login (email + contraseña) con validaciones, estados de carga y conexión
 * a Supabase para autenticar usuarios. Devuelvo token, userId y correo al login exitoso.
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
 * Pantalla de Login
 * Pantalla para que los usuarios inicien sesión con email y contraseña
 */
@Composable
fun PantallaLogin(
    alVolverAtras: () -> Unit = {},
    loginCorrecto: (String, String, String) -> Unit = { _, _, _ -> }
) {
    // Obtener contexto para mostrar Toast
    val context = LocalContext.current

    // Estado para email y contraseña
    val correo = remember { mutableStateOf("") }
    val contrasena = remember { mutableStateOf("") }

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
                // Ícono de bienvenida
                Text(
                    text = "🔐",
                    fontSize = 64.sp,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Título
                Text(
                    text = "Iniciar Sesión",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = KidsPlannerColors.TextPrimary,
                    textAlign = TextAlign.Center
                )

                // Espaciador
                Spacer(modifier = Modifier.height(12.dp))

                // Descripción
                Text(
                    text = "Accede a tu cuenta para continuar",
                    fontSize = 12.sp,
                    color = KidsPlannerColors.TextSecondary,
                    textAlign = TextAlign.Center
                )

                // Espaciador grande
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

                // Espaciador
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

                // Espaciador grande
                Spacer(modifier = Modifier.height(32.dp))

                // Botón: Iniciar Sesión
                PrimaryButton(
                    text = "Iniciar Sesión",
                    onClick = {
                        // Solo procesar si no está cargando
                        if (!isLoading.value) {
                            // Normaliza entradas eliminando espacios accidentales antes de validar
                            val correoSinEspacios = correo.value.trim()
                            val contrasenaSinEspacios = contrasena.value.trim()
                            if (correoSinEspacios != correo.value) {
                                correo.value = correoSinEspacios
                            }
                            if (contrasenaSinEspacios != contrasena.value) {
                                contrasena.value = contrasenaSinEspacios
                            }
                            // Validar que los campos no están vacíos
                            if (correoSinEspacios.isBlank() || contrasenaSinEspacios.isBlank()) {
                                Toast.makeText(context, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                            } else if (!correoSinEspacios.contains("@")) {
                                Toast.makeText(context, "Por favor ingresa un email válido", Toast.LENGTH_SHORT).show()
                            } else {
                                // Activar indicador de carga
                                isLoading.value = true
                                // Ejecutar login en un hilo secundario
                                CoroutineScope(Dispatchers.IO).launch {
                                    val authRepository = AuthRepository()
                                    val datosLogin = authRepository.iniciarSesion(correoSinEspacios, contrasenaSinEspacios)
                                    Log.d("PantallaLogin", "Resultado login: $datosLogin")

                                    // Mostrar resultado al usuario con Toast
                                    withContext(Dispatchers.Main) {
                                        // Desactivar indicador de carga
                                        isLoading.value = false

                                        // Procesar resultado con patrón nullable simple
                                        if (datosLogin != null) {
                                            Toast.makeText(context, "Inicio de sesión correcto", Toast.LENGTH_SHORT).show()
                                            loginCorrecto(datosLogin.token, datosLogin.userId, correoSinEspacios)  // Navegar al Dashboard con credenciales
                                        } else {
                                            Toast.makeText(context, "Error: Credenciales inválidas", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        }
                    }
                )

                // Espaciador pequeño
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

                // Espaciador pequeño
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
