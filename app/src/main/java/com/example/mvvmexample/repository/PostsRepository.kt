package com.example.mvvmexample.repository

import com.example.mvvmexample.cache.CacheMode
import com.example.mvvmexample.cache.CacheService
import com.example.mvvmexample.model.Post
import com.example.mvvmexample.model.PostList
import com.example.mvvmexample.service.PostsService
import com.example.mvvmexample.ui.LoadState
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * MVVMExample
 * Created by jake on 30/04/21, 3:17 PM
 */
class PostsRepository(
    private val postsService: PostsService,
    private val cacheService: CacheService
) {
    // StateFlow is the flow equivalent to LiveData
    private val mutablePostsState = MutableStateFlow<List<Post>>(listOf())
    private val mutableLoadState = MutableStateFlow<LoadState?>(null)

    // non-mutable version of the state flow
    val loadState = mutableLoadState.asStateFlow()

    // in memory list of deleted posts
    private val deletedPosts = mutableSetOf<Int>()

    // because this repo is a singleton, it probably makes sense for this to use GlobalScope
    // otherwise if we used a viewModel scope, it's possible that a shared request could get cancelled
    // if one of the viewModels was destroyed.
    // This function could arguably be private, as long as we were willing to create another function for "refresh" behaviour.
    fun fetchPosts(cacheMode: CacheMode = CacheMode.CacheAndUpdate) = GlobalScope.launch {
        mutableLoadState.emit(LoadState.Loading)
        val cached = cacheService.readFromCache(PostsKey, cacheMode)
        if (cached != null) {
            mutablePostsState.emit((cached.result as PostList).posts)
            mutableLoadState.emit(LoadState.Success)
            if (cached.shouldExit) { return@launch }
        }

        try {
            val posts = postsService.fetchPosts()
            val filteredPosts = posts.filter { !deletedPosts.contains(it.id) }
            mutablePostsState.emit(filteredPosts)
            mutableLoadState.emit(LoadState.Success)
            if (filteredPosts.isNotEmpty()) {
                cacheService.updateCache(PostsKey, PostList(filteredPosts))
            }
        } catch (e: Exception) {
            // it may be worth discussing how we expect error handling to work in this case?
                // Flow has a `.catch` case which may allow us to pass
                // exceptions up to the ViewModel level, rather than trying to keep track of it via mutableLoadState.

            // unsure if we should update mutablePostsState here or not.
            mutableLoadState.emit(LoadState.Error(e))
        }
    }

    // fetches the posts when the flow is grabbed by a subscriber.
    fun postsFlow(cacheMode: CacheMode = CacheMode.CacheAndUpdate): StateFlow<List<Post>> {
        GlobalScope.launch { fetchPosts(cacheMode) }
        return mutablePostsState
    }

    // simulates deleting a post by adding it to a "deleted posts" list in memory
    // This automatically clears the cache and forces a refresh, so all subscribers will be updated.
    fun deletePost(id: Int) {
        deletedPosts.add(id)
        cacheService.removeFromCache(PostsKey)
        GlobalScope.launch { fetchPosts(CacheMode.CacheAndUpdate) }
    }

    companion object {
        private const val PostsKey = "posts"
    }
}