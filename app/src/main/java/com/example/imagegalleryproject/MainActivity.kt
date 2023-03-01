package com.example.imagegalleryproject

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.imagegalleryproject.adapter.RecyclerAdapter
import com.example.imagegalleryproject.databinding.ActivityMainBinding
import com.example.imagegalleryproject.model.Image
import com.example.imagegalleryproject.viewmodel.ImageViewModel

class MainActivity: AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var pathList: ArrayList<String> //contains the paths of images
    private lateinit var recyclerAdapter: RecyclerAdapter
    private lateinit var viewModel: ImageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pathList = ArrayList<String>()

        viewModel = ViewModelProvider(this).get(ImageViewModel::class.java)

        if (checkPermission()) {
            Toast.makeText(this, "Permissions granted..", Toast.LENGTH_SHORT).show();
            getImagePath()
        } else {
            requestPermission()
        }

        recyclerAdapter = RecyclerAdapter(this)

        binding.rv.layoutManager = GridLayoutManager(this, 4)
        binding.rv.adapter = recyclerAdapter
    }

    fun getImagePath() {
        viewModel.getModelImage().observe(this, { imagePath ->
            recyclerAdapter.submitList(imagePath.path)
        })
        recyclerAdapter.notifyDataSetChanged()
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            200
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            200 -> {
                if (grantResults.size > 0) {
                    var permissionGrant = grantResults[0] == PackageManager.PERMISSION_GRANTED

                    if (permissionGrant) {
                        Toast.makeText(this, "Permissions Granted..", Toast.LENGTH_SHORT).show();
                        getImagePath()
                    } else {
                        Toast.makeText(this, "Permissions Denied..", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}