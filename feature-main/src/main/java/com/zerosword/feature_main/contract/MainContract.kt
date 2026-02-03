package com.zerosword.feature_main.contract

data class MainState(
    val isLoading: Boolean = true,
    val message: String = "",
    val isError: Boolean = false
)

sealed interface MainIntent {
    data object LoadData : MainIntent
    data object Retry : MainIntent
}

sealed interface MainSideEffect {
    data class ShowToast(val message: String) : MainSideEffect
}
