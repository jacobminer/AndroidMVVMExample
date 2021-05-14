package com.example.mvvmexample.repository

import com.example.mvvmexample.cache.CacheMode
import com.example.mvvmexample.cache.CacheService
import com.example.mvvmexample.extensions.*
import com.example.mvvmexample.model.Post
import com.example.mvvmexample.model.PostList
import com.example.mvvmexample.service.PostsService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
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
    private val mutablePostsFlow = MutableStateThrowableFlow<List<Post>>(listOf())

    // in memory list of deleted posts
    private val deletedPosts = mutableSetOf<Int>()

    // because this repo is a singleton, it probably makes sense for this to use GlobalScope
    // otherwise if we used a viewModel scope, it's possible that a shared request could get cancelled
    // if one of the viewModels was destroyed.
    // This function could arguably be private, as long as we were willing to create another function for "refresh" behaviour.
    private fun fetchPosts(cacheMode: CacheMode = CacheMode.CacheAndUpdate) = GlobalScope.launch {
        val cached = cacheService.readFromCache(PostsKey, cacheMode)
        if (cached != null) {
            mutablePostsFlow.emitData((cached.result as PostList).posts)
            if (cached.shouldExit) { return@launch }
        }

        mutablePostsFlow.emitFrom {
            val posts = postsService.fetchPosts()
            val filteredPosts = posts.filter { !deletedPosts.contains(it.id) }
            if (filteredPosts.isNotEmpty()) {
                cacheService.updateCache(PostsKey, PostList(filteredPosts))
            }
            filteredPosts
        }
    }

    // fetches the posts when the flow is grabbed by a subscriber.
    // TODO: 13/05/21: it might be worth coming up with a naming convention for functions returning a flow that we're expecting to share?
        // we should theoretically be able to tell based on the return type
        // (StateFlow for shared flows, Flow for one off flows that require params)
    fun postsFlow(cacheMode: CacheMode = CacheMode.CacheAndUpdate): StateThrowableFlow<List<Post>> {
        GlobalScope.launch { fetchPosts(cacheMode) }
        return mutablePostsFlow
    }

    // simulates deleting a post by adding it to a "deleted posts" list in memory
    // This automatically clears the cache and forces a refresh, so all subscribers will be updated.
    fun deletePost(id: Int) = flow {
        val successful = true
        if (successful) {
            deletedPosts.add(id)
            cacheService.removeFromCache(PostsKey)
            emit(Unit)
            fetchPosts(CacheMode.CacheAndUpdate)
        } else {
            throw RuntimeException("Fake exception")
        }
    }

    companion object {
        private const val PostsKey = "posts"
    }
}