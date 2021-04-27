package com.example.mvvmexample.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.mvvmexample.databinding.ActivityMainBinding
import com.example.mvvmexample.extensions.viewBinding
import dagger.hilt.android.AndroidEntryPoint

// our View
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    // lifecycle aware viewModel
    private val viewModel: MainViewModel by viewModels()

    // views are bound using dataBinding, which allows us to bind viewModel liveData directly
    // to the XML
    private val binding by viewBinding(ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        // sets the bindings viewModel and lifecycle
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }
}