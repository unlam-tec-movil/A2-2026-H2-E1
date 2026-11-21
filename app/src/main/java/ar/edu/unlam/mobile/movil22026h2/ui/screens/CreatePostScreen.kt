package ar.edu.unlam.mobile.movil22026h2.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import ar.edu.unlam.mobile.movil22026h2.ui.theme.Pink40
import ar.edu.unlam.mobile.movil22026h2.ui.theme.Pink80
import ar.edu.unlam.mobile.movil22026h2.ui.theme.Purple40
import ar.edu.unlam.mobile.movil22026h2.ui.theme.Purple80
import ar.edu.unlam.mobile.movil22026h2.ui.theme.PurpleGrey40
import ar.edu.unlam.mobile.movil22026h2.ui.theme.PurpleGrey80

const val CREATE_POST_SCREEN_ROUTE = "create_post"

@Composable
fun CreatePostScreen(
    modifier: Modifier = Modifier,
    viewModel: CreatePostViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
) {
    val uiState: CreatePostUIState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            val message =
                when (event) {
                    CreatePostEvent.PublishedOk -> "Publicado con éxito"
                    CreatePostEvent.PublishedError -> "No se pudo publicar"
                    CreatePostEvent.DraftSaved -> "Borrador guardado"
                }
            // Muestra el snackbar y espera a que se vea.
            snackbarHostState.showSnackbar(
                message = message,
                actionLabel = "OK",
                duration = SnackbarDuration.Short,
            )
            // Solo vuelve a Home si publicó bien.
            if (event is CreatePostEvent.PublishedOk) {
                onBack()
            }
        }
    }

    val backgroundBrush =
        Brush.verticalGradient(
            colors =
                listOf(
                    Purple80.copy(alpha = 0.20f),
                    Pink80.copy(alpha = 0.12f),
                    MaterialTheme.colorScheme.background,
                ),
        )

    Scaffold(
        modifier = modifier,
        containerColor = Color.Transparent,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Purple40,
                    contentColor = Color.White,
                    actionColor = Pink80,
                    shape = RoundedCornerShape(14.dp),
                )
            }
        },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(backgroundBrush)
                    .padding(padding)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Header con botón volver y título
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Purple40,
                    )
                }

                Box(
                    modifier =
                        Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Purple40),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.EditNote,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp),
                    )
                }

                Column {
                    Text(
                        text = "Nuevo post",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Purple40,
                    )
                    Text(
                        text = "Escribí y compartí con la comunidad",
                        style = MaterialTheme.typography.bodySmall,
                        color = PurpleGrey40,
                    )
                }
            }

            // Tarjeta / cuadrado que resalta del fondo para escribir el post
            Card(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.5.dp, Purple80.copy(alpha = 0.75f)),
            ) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                ) {
                    OutlinedTextField(
                        value = uiState.draftText,
                        onValueChange = viewModel::onDraftChanged,
                        enabled = !uiState.isLoading,
                        placeholder = {
                            Text(
                                text = "¿Qué querés compartir?",
                                color = PurpleGrey40.copy(alpha = 0.7f),
                            )
                        },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .weight(1f),
                        minLines = 5,
                        shape = RoundedCornerShape(16.dp),
                        colors =
                            OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Purple40,
                                unfocusedBorderColor = PurpleGrey80,
                                focusedLabelColor = Purple40,
                                cursorColor = Purple40,
                            ),
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        Text(
                            text = "${uiState.draftText.length} caracteres",
                            style = MaterialTheme.typography.labelMedium,
                            color = PurpleGrey40,
                        )
                    }
                }
            }

            // Botones: Guardar borrador y Publicar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                OutlinedButton(
                    onClick = { viewModel.onSaveDraft() },
                    enabled = uiState.canSaveDraft,
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.5.dp, Purple40),
                    colors =
                        ButtonDefaults.outlinedButtonColors(
                            contentColor = Purple40,
                            disabledContentColor = PurpleGrey80,
                        ),
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkBorder,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Guardar borrador",
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                Button(
                    onClick = { viewModel.onPublish() },
                    enabled = uiState.canPublish,
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(48.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = Purple40,
                            contentColor = Color.White,
                            disabledContainerColor = Purple80,
                            disabledContentColor = Color.White,
                        ),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Publicar",
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}