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
import com.example.mvvmexample.ui.ContentLoadViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * MVVMExample
 * Created by jake on 19/04/21, 3:21 PM
 */
@HiltViewModel
class PostsViewModel @Inject constructor(
    // injected via hilt
    private val usersRepository: UsersRepository,
    private val postsRepository: PostsRepository
): ViewModel() {
    // combines the load state of the two repos into one loading state for view consumption
    private val mutablePostsLoadState = MutableLiveData<ContentLoadViewState>(ContentLoadViewState.Loading)
    val postsLoadState = mutablePostsLoadState.asLiveData()

    // As soon as the postsFlow value changes, map will be called, and we'll re-fetch the list of users.
    // Once we have both, we can combine them and send a PostViewState list out via LiveData.
    val posts = postsRepository.postsFlow(CacheMode.CacheAndUpdate).catchError { e ->
        // handle any errors here
        // update the loading state
        e.printStackTrace()
        mutablePostsLoadState.postValue(ContentLoadViewState.Error(e))
    }.map { posts ->
        // update the loading state
        mutablePostsLoadState.postValue(ContentLoadViewState.Loading)
        // when the post list isn't empty, fetch the list of users
        if (posts.isEmpty()) { return@map listOf<PostViewState>() }
        val userIds = posts.map { it.userId }.toSet()
        // always try to read from cache here
        val users = usersRepository.fetchUsers(userIds, CacheMode.CacheOnly).catch { e ->
            // handle errors when fetching users here
            e.printStackTrace()
        }.firstOrNull() // just use the first result
        // update the loading state
        mutablePostsLoadState.postValue(ContentLoadViewState.Success)
        // combine the data from the users repo with the data from the posts repo to create the post view state
        posts.map { post ->
            PostViewState(post.id, post.title, users?.firstOrNull { it.id == post.userId }?.name ?: "Unknown")
        }
    }.asLiveData(viewModelScope.coroutineContext) // convert the flow into liveData, using the viewModel scope context

    // emulates deleting a post.
    fun deleteTapped(postId: Int) = viewModelScope.launch {
        postsRepository.deletePost(postId).catch { e ->
            // handle delete post error here
            e.printStackTrace()
        }.collect() // no return value, but we need to call collect, otherwise the steam won't occur
    }
}