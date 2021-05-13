package com.example.mvvmexample.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.mvvmexample.cache.CacheMode
import com.example.mvvmexample.repository.PostsRepository
import com.example.mvvmexample.repository.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
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
    // combines the load state of the two repos into one loading state for view consumption
    val loadState = postsRepository.loadState.combine(usersRepository.loadState) { postsLoadState, usersLoadState ->
        postsLoadState?.combined(usersLoadState)
    }.asLiveData(viewModelScope.coroutineContext) // convert the flow into liveData, using the viewModel scope context

    // As soon as the postsFlow value changes, mapLatest will be called, and we'll re-fetch the list of users.
    // Once we have both, we can combine them and send a PostViewState list out via LiveData.
    val posts = postsRepository.postsFlow(CacheMode.CacheAndUpdate).mapLatest { posts ->
        // when the posts aren't empty, fetch the list of users
        if (posts.isEmpty()) {
            return@mapLatest listOf<PostViewState>()
        }
        val userIds = posts.map { it.userId }.toSet()
        // always try to read from cache here
        val users = usersRepository.fetchUsers(userIds, CacheMode.CacheOnly).firstOrNull()
        // combine the data from the users repo with the data from the posts repo to create the post view state
        posts.map { post ->
            PostViewState(post.id, post.title, users?.firstOrNull { it.id == post.userId }?.name ?: "Unknown")
        }
    }.asLiveData(viewModelScope.coroutineContext)

    // calls refresh, allowing the cached value to be displayed
    fun refreshTapped() = viewModelScope.launch {
        postsRepository.fetchPosts()
    }

    // emulates deleting a post. In a real application, we'd probably pass in a PostViewState object into this function
        // rather than using the LiveData.
    fun deleteTapped() = viewModelScope.launch {
        val post = posts.value?.firstOrNull() ?: return@launch
        postsRepository.deletePost(post.id)
    }
}