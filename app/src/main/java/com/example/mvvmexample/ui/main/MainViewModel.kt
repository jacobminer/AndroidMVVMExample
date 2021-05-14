package com.example.mvvmexample.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.mvvmexample.cache.CacheMode
import com.example.mvvmexample.extensions.asLiveData
import com.example.mvvmexample.extensions.catchError
import com.example.mvvmexample.repository.PostsRepository
import com.example.mvvmexample.repository.UsersRepository
import com.example.mvvmexample.ui.LoadState
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
    private val mutablePostsLoadState = MutableLiveData<LoadState>(LoadState.Loading)
    val postsLoadState = mutablePostsLoadState.asLiveData()

    // As soon as the postsFlow value changes, mapLatest will be called, and we'll re-fetch the list of users.
    // Once we have both, we can combine them and send a PostViewState list out via LiveData.
    val posts = postsRepository.postsFlow(CacheMode.CacheAndUpdate).catchError { e ->
        // handle any errors here
        // update the loading state
        mutablePostsLoadState.postValue(LoadState.Error(e))
    }.map { posts ->
        // update the loading state
        mutablePostsLoadState.postValue(LoadState.Loading)
        // when the post list isn't empty, fetch the list of users
        if (posts.isEmpty()) { return@map listOf<PostViewState>() }
        val userIds = posts.map { it.userId }.toSet()
        // always try to read from cache here
        val users = usersRepository.fetchUsers(userIds, CacheMode.CacheOnly).catch {
            // handle errors when fetching users here
        }.firstOrNull() // just use the first result
        // update the loading state
        mutablePostsLoadState.postValue(LoadState.Success)
        // combine the data from the users repo with the data from the posts repo to create the post view state
        posts.map { post ->
            PostViewState(post.id, post.title, users?.firstOrNull { it.id == post.userId }?.name ?: "Unknown")
        }
    }.asLiveData(viewModelScope.coroutineContext) // convert the flow into liveData, using the viewModel scope context

    // calls refresh, allowing the cached value to be displayed
    fun refreshTapped() = viewModelScope.launch {
        postsRepository.fetchPosts()
    }

    // emulates deleting a post. In a real application, we'd probably pass in a PostViewState object into this function
        // rather than using the LiveData directly.
    fun deleteTapped() = viewModelScope.launch {
        val post = posts.value?.firstOrNull() ?: return@launch
        postsRepository.deletePost(post.id).catch {
            // handle delete post error here
        }.collect() // no return value, but we need to call collect, otherwise the steam won't occur
    }
}