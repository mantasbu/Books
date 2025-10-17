package com.mantasbu.books.domain.core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException

inline fun <DB, REMOTE> networkBoundResource(
    crossinline query: () -> Flow<DB>,
    crossinline fetch: suspend () -> REMOTE,
    crossinline saveFetchResult: suspend (REMOTE) -> Unit,
    crossinline shouldFetch: suspend (DB?) -> Boolean = { true },
): Flow<Resource<DB>> = flow {
    emit(Resource.Loading())

    val cached: DB? = query().firstOrNull()
    if (cached != null) {
        emit(Resource.Success(cached))
    }

    if (shouldFetch(cached)) {
        try {
            val remote = fetch()
            saveFetchResult(remote)
        } catch (t: Throwable) {
            emit(Resource.Error(t.toUserMessage()))
        }
    }

    emitAll(query().map { Resource.Success(it) })
}

/** Map exceptions to a readable UI message */
fun Throwable.toUserMessage(): String = when (this) {
    is HttpException -> "Network error ${code()}"
    is java.net.UnknownHostException -> "No internet connection"
    is java.net.SocketTimeoutException -> "Request timed out"
    else -> message ?: "Unexpected error"
}