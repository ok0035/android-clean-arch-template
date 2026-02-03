package com.zerosword.feature_main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zerosword.domain.reporitory.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MainUiState(
    val isLoading: Boolean = true,
    val message: String = "",
    val isError: Boolean = false
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = MainUiState(isLoading = true)

            mainRepository.getData()
                .onSuccess { data ->
                    _uiState.value = MainUiState(
                        isLoading = false,
                        message = data
                    )
                }
                .onFailure { error ->
                    _uiState.value = MainUiState(
                        isLoading = false,
                        message = error.message ?: "Unknown error",
                        isError = true
                    )
                }
        }
    }
}
