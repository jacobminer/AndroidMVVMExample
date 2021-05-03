package com.example.mvvmexample.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.mvvmexample.cache.CacheMode
import com.example.mvvmexample.repository.PostsRepository
import com.example.mvvmexample.repository.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * MVVMExample
 * Created by jake on 19/04/21, 3:21 PM
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    // injected via hilt
    private val usersRepository: UsersRepository,
    private val postsRepository: PostsRepository
): ViewModel() {
    // this maps the counterState flow to a liveData for view consumption
    // as soon as counterState changes, mapLatest will be called, and the result will be
    // sent via the exampleText LiveData

    // combine the load state of the two calls
    val loadState = postsRepository.loadState.combine(usersRepository.loadState) { postsLoadState, usersLoadState ->
        postsLoadState?.combined(usersLoadState)
    }.asLiveData()

    private val posts = postsRepository.postsFlow(CacheMode.CacheAndUpdate).debounce(300).mapLatest {
        val userIds = it.map { it.userId }.toSet()
        usersRepository.fetchUsers(userIds, CacheMode.CacheOnly)
        it
    }

    val users = usersRepository.usersFlow().mapLatest { users ->
        (posts.firstOrNull() ?: listOf()).map { post ->
            PostViewState(post.id, post.title, users.firstOrNull { it.id == post.userId }?.name ?: "Unknown")
        }
    }.asLiveData()

    // calls refresh, allowing the cached value to be displayed
    fun refreshTapped() = viewModelScope.launch {
        postsRepository.fetchPosts()
    }

    // increments the counter
    fun incrementTapped() = viewModelScope.launch {

    }
}