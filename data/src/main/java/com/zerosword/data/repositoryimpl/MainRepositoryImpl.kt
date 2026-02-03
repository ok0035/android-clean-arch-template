package com.zerosword.data.repositoryimpl

import com.skydoves.sandwich.message
import com.skydoves.sandwich.suspendOnFailure
import com.skydoves.sandwich.suspendOnSuccess
import com.zerosword.data.services.MainService
import com.zerosword.domain.reporitory.MainRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepositoryImpl @Inject constructor(
    private val mainService: MainService
) : MainRepository {

    override fun getData(): Flow<String> = flow {
        mainService.getData()
            .suspendOnSuccess {
                emit("Nice to meet you ${(this.data.origin ?: "")}")
            }.suspendOnFailure {
                throw RuntimeException(this.message())
            }
    }
}
