package com.example.mvvmexample.repository

import com.example.mvvmexample.cache.CacheMode
import com.example.mvvmexample.cache.CacheService
import com.example.mvvmexample.model.UserList
import com.example.mvvmexample.service.UsersService
import com.example.mvvmexample.ui.LoadState
import kotlinx.coroutines.flow.*

/**
 * MVVMExample
 * Created by jake on 30/04/21, 3:18 PM
 */
class UsersRepository(
    private val usersService: UsersService,
    private val cacheService: CacheService
) {
    private val mutableLoadState = MutableStateFlow<LoadState?>(null)

    val loadState = mutableLoadState.asSharedFlow()

    // Returns a one-off flow, which can then be subscribed to.
    // TODO: 13/05/21: This will cause any object subscribed to mutableLoadState to update their load state.
        // which may or may not be the behaviour we're generally looking for.
    fun fetchUsers(userIds: Set<Int>, cacheMode: CacheMode = CacheMode.CacheAndUpdate) = flow {
        mutableLoadState.emit(LoadState.Loading)
        val cached = cacheService.readFromCache(UsersKey, cacheMode)
        if (cached != null) {
            emit((cached.result as UserList).users)
            mutableLoadState.emit(LoadState.Success)
            if (cached.shouldExit) { return@flow }
        }

        try {
            val users = usersService.fetchUsers().filter { userIds.contains(it.id) }
            emit(users)
            mutableLoadState.emit(LoadState.Success)
            if (users.isNotEmpty()) {
                cacheService.updateCache(UsersKey, UserList(users))
            }
        } catch (e: Exception) {
            mutableLoadState.emit(LoadState.Error(e))
        }
    }

    companion object {
        private const val UsersKey = "users"
    }
}