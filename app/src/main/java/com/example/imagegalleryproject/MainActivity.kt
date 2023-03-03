package com.example.imagegalleryproject

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.imagegalleryproject.adapter.RecyclerAdapter
import com.example.imagegalleryproject.databinding.ActivityMainBinding
import com.example.imagegalleryproject.model.Image
import com.example.imagegalleryproject.viewmodel.ImageViewModel
import kotlin.coroutines.coroutineContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
   lateinit var viewModel: ImageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.imageNavHostContainer)

        val config = AppBarConfiguration(navController.graph)

        binding.toolbar.setupWithNavController(navController, config)

        viewModel = ViewModelProvider(this).get(ImageViewModel::class.java)

    }
}