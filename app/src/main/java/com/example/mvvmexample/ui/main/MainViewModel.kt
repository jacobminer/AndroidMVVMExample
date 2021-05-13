package com.example.mvvmexample.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.mvvmexample.cache.CacheMode
import com.example.mvvmexample.model.Post
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
    private var fetchedPosts = listOf<Post>()

    // combines the load state of the two repos into one loading state for view consumption
    val loadState = postsRepository.loadState.combine(usersRepository.loadState) { postsLoadState, usersLoadState ->
        postsLoadState?.combined(usersLoadState)
    }.asLiveData()

    // filter the list of posts using the list of users, then create a view state to show on the UI
    // as soon as the usersFlow value changes, mapLatest will be called, and the result will be
    // sent via the exampleText LiveData
    val posts = postsRepository.postsFlow(CacheMode.CacheAndUpdate).mapLatest { posts ->
        // maps the list of posts from the postsRepo to a private member variable
        fetchedPosts = posts
        // when the posts aren't empty, fetch the list of users
        if (posts.isEmpty()) {
            return@mapLatest listOf<PostViewState>()
        }
        val userIds = posts.map { it.userId }.toSet()
        val users = usersRepository.fetchUsers(userIds, CacheMode.CacheOnly).singleOrNull()
        posts.map { post ->
            PostViewState(post.id, post.title, users?.firstOrNull { it.id == post.userId }?.name ?: "Unknown")
        }
    }.asLiveData()

    // calls refresh, allowing the cached value to be displayed
    fun refreshTapped() = viewModelScope.launch {
        postsRepository.fetchPosts()
    }

    // emulates deleting a post
    fun deleteTapped() = viewModelScope.launch {
        val post = fetchedPosts.firstOrNull() ?: return@launch
        postsRepository.deletePost(post.id)
    }
}