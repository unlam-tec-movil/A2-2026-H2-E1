package ar.edu.unlam.mobile.movil22026h2.ui.screens

import androidx.lifecycle.ViewModel
import ar.edu.unlam.mobile.movil22026h2.data.repositories.FavoriteUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel // Hilt lo crea; la  pantalla lo pide con hiltViewModel()
class FavoriteUsersViewModel
    @Inject
    constructor(
        private val repository: FavoriteUserRepository, // depende de la INTERFACE, no de Room
    ) : ViewModel()
