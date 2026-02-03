package com.zerosword.domain.reporitory

interface MainRepository {
    suspend fun getData(): Result<String>
}
