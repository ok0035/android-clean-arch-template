package com.zerosword.data.repositoryimpl

import com.skydoves.sandwich.ApiResponse
import com.zerosword.data.services.MainService
import com.zerosword.domain.reporitory.MainRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepositoryImpl @Inject constructor(
    private val mainService: MainService
) : MainRepository {

    override suspend fun getData(): Result<String> {
        return when (val response = mainService.getData()) {
            is ApiResponse.Success -> {
                Result.success("Nice to meet you ${response.data.origin ?: ""}")
            }
            is ApiResponse.Failure -> {
                Result.failure(RuntimeException(response.toString()))
            }
        }
    }
}
