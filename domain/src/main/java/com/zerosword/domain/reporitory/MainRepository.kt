package com.zerosword.domain.reporitory

import kotlinx.coroutines.flow.Flow

interface MainRepository {
    fun getData(): Flow<String>
}
