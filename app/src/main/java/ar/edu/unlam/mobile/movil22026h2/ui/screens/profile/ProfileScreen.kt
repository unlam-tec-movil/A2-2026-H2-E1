package ar.edu.unlam.mobile.movil22026h2.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import ar.edu.unlam.mobile.movil22026h2.ui.components.Avatar
import ar.edu.unlam.mobile.movil22026h2.ui.components.ErrorView
import ar.edu.unlam.mobile.movil22026h2.ui.components.LoadingView

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onError: (Throwable) -> Unit,
    onMessage: (String) -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ProfileEvent.Saved -> onMessage(event.message)
                is ProfileEvent.Failed -> onError(event.exception)
            }
        }
    }

    when (val state = uiState) {
        ProfileUIState.Loading -> {
            LoadingView(modifier)
        }

        is ProfileUIState.Error -> {
            ErrorView(state.message, onRetry = viewModel::loadProfile, modifier = modifier)
        }

        is ProfileUIState.Success -> {
            ProfileContent(
                state = state,
                onNameChange = viewModel::onNameChange,
                onAvatarUrlChange = viewModel::onAvatarUrlChange,
                onPasswordChange = viewModel::onPasswordChange,
                onSave = viewModel::saveProfile,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun ProfileContent(
    state: ProfileUIState.Success,
    onNameChange: (String) -> Unit,
    onAvatarUrlChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Avatar(name = state.name, avatarUrl = state.avatarUrl, size = 96.dp)

        OutlinedTextField(
            value = state.name,
            onValueChange = onNameChange,
            label = { Text("Nombre") },
            singleLine = true,
            enabled = !state.isSaving,
            modifier = Modifier.fillMaxWidth(),
        )

        OutlinedTextField(
            value = state.email,
            onValueChange = {},
            label = { Text("Email") },
            singleLine = true,
            enabled = false,
            modifier = Modifier.fillMaxWidth(),
        )

        OutlinedTextField(
            value = state.avatarUrl,
            onValueChange = onAvatarUrlChange,
            label = { Text("URL del avatar") },
            singleLine = true,
            enabled = !state.isSaving,
            modifier = Modifier.fillMaxWidth(),
        )

        OutlinedTextField(
            value = state.password,
            onValueChange = onPasswordChange,
            label = { Text("Nueva contraseña (opcional)") },
            singleLine = true,
            enabled = !state.isSaving,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
        )

        Button(
            onClick = onSave,
            enabled = !state.isSaving && state.name.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (state.isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(if (state.isSaving) "Guardando..." else "Guardar")
        }
    }
}
