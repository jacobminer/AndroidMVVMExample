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
    private val mutablePostsState = MutableStateFlow<List<Post>>(emptyList())
    private val mutableLoadState = MutableStateFlow<LoadState?>(null)

    val loadState = mutableLoadState.asStateFlow()

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
            mutablePostsState.emit(posts)
            mutableLoadState.emit(LoadState.Success)
            if (posts.isNotEmpty()) {
                cacheService.updateCache(PostsKey, PostList(posts))
            }
        } catch (e: Exception) {
            mutableLoadState.emit(LoadState.Error(e))
        }
    }

    fun postsFlow(cacheMode: CacheMode = CacheMode.CacheAndUpdate): StateFlow<List<Post>> {
        fetchPosts(cacheMode)
        return mutablePostsState
    }

    companion object {
        private const val PostsKey = "posts"
    }
}