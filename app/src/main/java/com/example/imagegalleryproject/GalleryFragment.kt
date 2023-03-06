package com.example.imagegalleryproject

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.imagegalleryproject.adapter.RecyclerAdapter
import com.example.imagegalleryproject.databinding.FragmentGalleryBinding
import com.example.imagegalleryproject.db.DatabaseInstance
import com.example.imagegalleryproject.db.ImageDao
import com.example.imagegalleryproject.model.Image
import com.example.imagegalleryproject.viewmodel.ImageViewModel
import com.example.imagegalleryproject.viewmodel.ImageViewModelFactory
import java.util.*
import kotlin.collections.ArrayList

class GalleryFragment: Fragment() {
    private lateinit var binding: FragmentGalleryBinding
    private lateinit var recyclerAdapter: RecyclerAdapter
    private lateinit var viewModel: ImageViewModel
    private lateinit var factory: ImageViewModelFactory
    private lateinit var imageDao: ImageDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentGalleryBinding.inflate(inflater, container, false)

        imageDao = DatabaseInstance.getInstance(requireActivity()).imageDao()

        factory = ImageViewModelFactory(activity?.application!!, imageDao)

        viewModel = ViewModelProvider(this, factory).get(ImageViewModel::class.java)

        if (checkPermission()) {
            Toast.makeText(requireContext(), "Permissions granted..", Toast.LENGTH_SHORT).show();
            viewModel.getImages()
            getImagePath()
        } else {
            requestPermission()
        }

        return binding.root
    }
    fun getImagePath() {
        recyclerAdapter = RecyclerAdapter(requireContext()) {
                selectedItem: Image -> listItemClicked(selectedItem)
        }
        binding.rv.layoutManager = GridLayoutManager(activity, 4)
        binding.rv.setHasFixedSize(true)
        binding.rv.adapter = recyclerAdapter
        viewModel.imagePathData.observe(viewLifecycleOwner, {
            recyclerAdapter.differ.submitList(it)
//            recyclerAdapter.notifyDataSetChanged() //is an expensive process, as it recreates (refereshes all rows) ViewHolder recycled by the RecyclerView
        })
    }


    private fun checkPermission(): Boolean {
        val readImagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) android.Manifest.permission.READ_MEDIA_IMAGES else android.Manifest.permission.READ_EXTERNAL_STORAGE

        return ContextCompat.checkSelfPermission(
            requireActivity(),
            readImagePermission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        requestPermissions(
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.READ_MEDIA_IMAGES, android.Manifest.permission.READ_MEDIA_VIDEO),
            200
        )
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            200 -> {
                if (grantResults.size > 0) {
                    var permissionGrant = grantResults[0] == PackageManager.PERMISSION_GRANTED

                    if (permissionGrant) {
                        Toast.makeText(context, "Permissions Granted..", Toast.LENGTH_SHORT).show();
                        viewModel.getImages()
                        getImagePath()
                    } else {
                        Toast.makeText(context, "Permissions Denied..", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun listItemClicked(image: Image) {
        val bundle = bundleOf("img_path" to image.path)
        binding.root.findNavController()
            .navigate(R.id.action_galleryFragment_to_imageFragment, bundle)
    }
}