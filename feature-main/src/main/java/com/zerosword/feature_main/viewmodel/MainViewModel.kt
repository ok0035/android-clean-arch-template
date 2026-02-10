package com.zerosword.feature_main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zerosword.domain.reporitory.MainRepository
import com.zerosword.feature_main.contract.MainIntent
import com.zerosword.feature_main.contract.MainSideEffect
import com.zerosword.feature_main.contract.MainState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state.asStateFlow()

    private val _sideEffect = Channel<MainSideEffect>(Channel.BUFFERED)
    val sideEffect = _sideEffect.receiveAsFlow()

    init {
        handleIntent(MainIntent.LoadData)
    }

    fun handleIntent(intent: MainIntent) {
        when (intent) {
            is MainIntent.LoadData -> loadData()
            is MainIntent.Retry -> loadData()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, isError = false)

            mainRepository.getData()
                .catch { e ->
                    val errorMessage = e.message ?: "Unknown error"
                    _state.value = _state.value.copy(
                        isLoading = false,
                        message = errorMessage,
                        isError = true
                    )
                    _sideEffect.send(MainSideEffect.ShowToast(errorMessage))
                }
                .collect { data ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        message = data,
                        isError = false
                    )
                }
        }
    }
}
