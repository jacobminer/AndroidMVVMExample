package com.example.mvvmexample.repository

import com.example.mvvmexample.cache.CacheMode
import com.example.mvvmexample.cache.CacheService
import com.example.mvvmexample.model.UserList
import com.example.mvvmexample.service.UsersService
import kotlinx.coroutines.flow.*

/**
 * MVVMExample
 * Created by jake on 30/04/21, 3:18 PM
 */
class UsersRepository(
    private val usersService: UsersService,
    private val cacheService: CacheService
) {
    // Returns a one-off flow, which can then be subscribed to.
    // Error handling should be done using `.catch { }` by the subscriber on the flow
    fun fetchUsers(userIds: Set<Int>, cacheMode: CacheMode = CacheMode.CacheAndUpdate) = flow {
        val cached = cacheService.readFromCache(UsersKey, cacheMode)
        if (cached != null) {
            emit((cached.result as UserList).users)
            if (cached.shouldExit) { return@flow }
        }

        val users = usersService.fetchUsers().filter { userIds.contains(it.id) }
        if (users.isNotEmpty()) { cacheService.updateCache(UsersKey, UserList(users)) }
        emit(users)
    }

    companion object {
        private const val UsersKey = "users"
    }
}