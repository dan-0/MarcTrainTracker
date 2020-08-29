package com.idleoffice.marctrain.ui.alertdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.idleoffice.marctrain.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

class AlertDetailsViewModel(
    private val dispatchers: DispatcherProvider,
    private val repo: AlertDetailsRepo
) : ViewModel() {

    private val _state = MutableStateFlow<AlertDetailsViewState>(AlertDetailsViewState.Init)
    val state: StateFlow<AlertDetailsViewState> = _state

    private val repoExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.e(throwable)
        _state.value = AlertDetailsViewState.Error
    }

    init {
        viewModelScope.launch(dispatchers.io) {
            observeRepo()
        }
    }

    fun loadDetails(detailsUrl: String) {
        viewModelScope.launch(dispatchers.io + repoExceptionHandler) {
            _state.value = AlertDetailsViewState.Loading
            repo.loadDetails(detailsUrl)
        }
    }

    private suspend fun observeRepo() {
        repo.data.collect {
            when (it) {
                AlertDetailsRepoState.Init -> null
                AlertDetailsRepoState.Error -> AlertDetailsViewState.Error
                is AlertDetailsRepoState.Content -> AlertDetailsViewState.Content(it.details)
            }?.also {
                _state.value = it
            }
        }
    }
}