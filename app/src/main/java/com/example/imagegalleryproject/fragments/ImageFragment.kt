package com.example.imagegalleryproject.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.imagegalleryproject.MainActivity
import com.example.imagegalleryproject.adapter.FavRecyclerAdapter
import com.example.imagegalleryproject.databinding.FragmentImageBinding
import com.example.imagegalleryproject.db.FavoriteDao
import com.example.imagegalleryproject.db.FavoriteDatabaseInstance
import com.example.imagegalleryproject.viewmodel.FavoriteViewModel

class ImageFragment: Fragment() {
    private lateinit var scaleGestureDetector: ScaleGestureDetector //for zooming on the image passed from the recyclerview //to be done in the fragment that shows selected image
    private var binding: FragmentImageBinding? = null
    private lateinit var adapter: FavRecyclerAdapter
    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var favoriteDao: FavoriteDao

    private val binding1 get() = binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(context, MainActivity::class.java))
            }
        }
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentImageBinding.inflate(inflater, container, false)
//        imgPath = requireArguments().getString("img_path")!! //to be done in a fragment that shows selected image


        favoriteDao = FavoriteDatabaseInstance.getInstance(requireActivity()).imageDao()

        favoriteViewModel =
            ViewModelProvider(requireActivity()).get(FavoriteViewModel::class.java)

        binding1.favRv.layoutManager = GridLayoutManager(requireContext(), 4)
        binding1.favRv.setHasFixedSize(true)
        adapter = FavRecyclerAdapter()
        binding1.favRv.adapter = adapter
        favoriteViewModel.fetchedImages.observe(viewLifecycleOwner, {
            adapter.differ.submitList(it)
        })

        val thisContext = container!!.context
        scaleGestureDetector = ScaleGestureDetector(thisContext, ScaleListener())

        return binding1.root
    }


    private class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            return super.onScale(detector)

            var newScaleFactor = detector.scaleFactor
            newScaleFactor = newScaleFactor * detector.scaleFactor

            return true
        }
    }


//    public fun getFavImages() {
//        val favImageList = viewModel.getImages()
//    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}