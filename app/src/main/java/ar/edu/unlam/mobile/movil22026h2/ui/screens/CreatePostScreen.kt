package ar.edu.unlam.mobile.movil22026h2.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

const val CREATE_POST_SCREEN_ROUTE = "create_post"

@Composable
fun CreatePostScreen(
    modifier: Modifier = Modifier,
    viewModel: CreatePostViewModel = hiltViewModel(),
    onPostPublished: () -> Unit = {},
) {
    val uiState: CreatePostUIState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            val message =
                when (event) {
                    is CreatePostEvent.PublishedOk -> "Publicado con éxito"
                    is CreatePostEvent.PublishedError -> "No se pudo publicar"
                    is CreatePostEvent.DraftSaved -> "Borrador guardado"
                }
            // Muestra el snackbar y espera a que se vea
            val result =
                snackbarHostState.showSnackbar(
                    message = message,
                    actionLabel = "OK",
                    duration = SnackbarDuration.Short,
                )
            // Solo vuelve a Home si publicó bien
            if (event is CreatePostEvent.PublishedOk) {
                onBack()
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Color(0xFF2F4A5A), // azul oscuro
                    contentColor = Color.White,
                    actionColor = Color(0xFF9AC7D9),
                    shape = RoundedCornerShape(8.dp),
                )
            }
        },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                }
                Text("Nuevo post", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }

            OutlinedTextField(
                value = uiState.draftText,
                onValueChange = viewModel::onDraftChanged,
                placeholder = { Text("¿Qué querés compartir?", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth().weight(1f),
                minLines = 5,
            )

            // Botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedButton(
                    onClick = { viewModel.onSaveDraft() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(50),
                ) { Text("Guardar borrador") }

                Button(
                    onClick = { viewModel.onPublish() },
                    enabled = uiState.draftText.isNotBlank(),
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F4A5A)),
                    shape = RoundedCornerShape(50),
                ) { Text("Publicar") }
            }
        }
    }
}
