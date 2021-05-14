package com.example.mvvmexample.ui.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mvvmexample.ui.LoadState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    // lifecycle aware viewModel
    private val viewModel: MainViewModel by viewModels()

    // if we were using viewBinding instead of Compose
//    private val binding by viewBinding(ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // if we were using viewBinding instead of Compose
//        setContentView(binding.root)
//        // sets the bindings viewModel and lifecycle
//        binding.viewModel = viewModel
//        binding.lifecycleOwner = this

        setContent {
            val loadState = viewModel.postsLoadState.observeAsState()
            val postsState = viewModel.posts.observeAsState()
            val posts = postsState.value ?: listOf()
            PostsScreen(loadState = loadState.value ?: LoadState.Loading, posts = posts)
        }
    }

    @Composable
    private fun PostsScreen(loadState: LoadState, posts: List<PostViewState>) {
        when (loadState) {
            is LoadState.Error -> TODO()
            LoadState.Loading -> LoadingIndicator()
            LoadState.Success -> PostsList(posts)
        }
    }

    @Composable
    private fun LoadingIndicator() {
        Box(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }

    @Composable
    private fun PostsList(posts: List<PostViewState>) {
        if (posts.isEmpty()) {
            Box(
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(text = "No items")
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(10.dp)) {
                items(posts, { it.id }) { post ->
                    PostView(post)
                }
            }
        }
    }

    @Composable
    private fun PostView(post: PostViewState) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = post.text, modifier = Modifier
                .weight(2f)
                .padding(end = 20.dp))
            Text(text = post.user, maxLines = 1, modifier = Modifier
                .weight(1f)
                .padding(end = 10.dp))
            Button(onClick = { viewModel.deleteTapped(post.id) }) {
                Text("X")
            }
        }
    }

    @Preview
    @Composable
    private fun LoadingIndicatorPreview() {
        LoadingIndicator()
    }

    @Preview
    @Composable
    private fun PostsEmptyListPreview() {
        PostsList(posts = listOf())
    }

    @Preview
    @Composable
    fun PostsListPreview() {
        PostsList(posts = listOf(
            PostViewState(0, "Test", "Test User"),
            PostViewState(1, "Test 2", "Test User 2")
        ))
    }
}