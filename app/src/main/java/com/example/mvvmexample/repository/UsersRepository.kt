package com.example.mvvmexample.repository

import com.example.mvvmexample.cache.CacheMode
import com.example.mvvmexample.cache.CacheService
import com.example.mvvmexample.model.User
import com.example.mvvmexample.model.UserList
import com.example.mvvmexample.service.UsersService
import com.example.mvvmexample.ui.LoadState
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception

/**
 * MVVMExample
 * Created by jake on 30/04/21, 3:18 PM
 */
class UsersRepository(
    private val usersService: UsersService,
    private val cacheService: CacheService
) {
    private val mutableUsersState = MutableStateFlow<List<User>>(emptyList())
    private val mutableLoadState = MutableStateFlow<LoadState?>(null)

    val loadState = mutableLoadState.asStateFlow()

    fun fetchUsers(userIds: Set<Int>, cacheMode: CacheMode = CacheMode.CacheAndUpdate) = GlobalScope.launch {
        mutableLoadState.emit(LoadState.Loading)
        val cached = cacheService.readFromCache(UsersKey, cacheMode)
        if (cached != null) {
            mutableUsersState.emit((cached.result as UserList).users)
            mutableLoadState.emit(LoadState.Success)
            if (cached.shouldExit) { return@launch }
        }

        try {
            val users = usersService.fetchUsers().filter { userIds.contains(it.id) }
            mutableUsersState.emit(users)
            mutableLoadState.emit(LoadState.Success)
            cacheService.updateCache(UsersKey, UserList(users))
        } catch (e: Exception) {
            mutableLoadState.emit(LoadState.Error(e))
        }
    }

    fun usersFlow(): StateFlow<List<User>> {
        return mutableUsersState.asStateFlow()
    }

    companion object {
        private const val UsersKey = "users"
    }
}