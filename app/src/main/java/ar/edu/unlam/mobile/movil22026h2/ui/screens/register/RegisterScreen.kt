package ar.edu.unlam.mobile.movil22026h2.ui.screens.register

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAddAlt1
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ar.edu.unlam.mobile.movil22026h2.ui.theme.Pink40
import ar.edu.unlam.mobile.movil22026h2.ui.theme.Pink80
import ar.edu.unlam.mobile.movil22026h2.ui.theme.Purple40
import ar.edu.unlam.mobile.movil22026h2.ui.theme.Purple80
import ar.edu.unlam.mobile.movil22026h2.ui.theme.PurpleGrey40
import ar.edu.unlam.mobile.movil22026h2.ui.theme.PurpleGrey80

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = hiltViewModel(),
    onRegisterSuccess: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onRegisterSuccess()
        }
    }

    val backgroundBrush =
        Brush.verticalGradient(
            colors =
                listOf(
                    Pink80.copy(alpha = 0.22f),
                    Purple80.copy(alpha = 0.16f),
                    MaterialTheme.colorScheme.background,
                ),
        )

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 28.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Pink40),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.PersonAddAlt1,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp),
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Crear cuenta",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Purple40,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Sumate a TUITER APP",
                style = MaterialTheme.typography.bodyMedium,
                color = PurpleGrey40,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Cuadrado / tarjeta que resalta del fondo con los datos de registro
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.5.dp, Pink80.copy(alpha = 0.8f)),
            ) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Tus datos",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Purple40,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = state.name,
                        onValueChange = { viewModel.onNameChanged(it) },
                        label = { Text("Nombre") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Purple40,
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors =
                            OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Purple40,
                                unfocusedBorderColor = PurpleGrey80,
                                focusedLabelColor = Purple40,
                                cursorColor = Purple40,
                            ),
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = state.email,
                        onValueChange = { viewModel.onEmailChanged(it) },
                        label = { Text("Email") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = Purple40,
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        isError = state.emailError != null,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors =
                            OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Purple40,
                                unfocusedBorderColor = PurpleGrey80,
                                focusedLabelColor = Purple40,
                                errorBorderColor = Pink40,
                                errorLabelColor = Pink40,
                                errorLeadingIconColor = Pink40,
                                cursorColor = Purple40,
                            ),
                    )
                    if (state.emailError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = state.emailError ?: (state.errorMessage ?: ""),
                            color = Pink40,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = state.password,
                        onValueChange = { viewModel.onPasswordChanged(it) },
                        label = { Text("Contraseña") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Purple40,
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector =
                                        if (passwordVisible) {
                                            Icons.Default.VisibilityOff
                                        } else {
                                            Icons.Default.Visibility
                                        },
                                    contentDescription =
                                        if (passwordVisible) {
                                            "Ocultar contraseña"
                                        } else {
                                            "Mostrar contraseña"
                                        },
                                    tint = PurpleGrey40,
                                )
                            }
                        },
                        visualTransformation =
                            if (passwordVisible) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors =
                            OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Purple40,
                                unfocusedBorderColor = PurpleGrey80,
                                focusedLabelColor = Purple40,
                                cursorColor = Purple40,
                            ),
                    )

                    if (state.errorMessage != null) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = Pink80.copy(alpha = 0.28f),
                            border = BorderStroke(1.dp, Pink40.copy(alpha = 0.5f)),
                        ) {
                            Text(
                                text = state.errorMessage ?: "",
                                color = Pink40,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(12.dp),
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    val isFormValid =
                        state.name.isNotBlank() &&
                            state.email.isNotBlank() &&
                            state.password.isNotBlank() &&
                            state.emailError == null

                    Button(
                        onClick = { viewModel.register() },
                        enabled = isFormValid && !state.isLoading,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = Purple40,
                                contentColor = Color.White,
                                disabledContainerColor = Purple80,
                                disabledContentColor = Color.White,
                            ),
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp,
                            )
                        } else {
                            Text(
                                text = "Registrarme",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }
        }
    }
}
