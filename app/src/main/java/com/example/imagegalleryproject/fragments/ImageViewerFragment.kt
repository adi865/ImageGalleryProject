package com.example.imagegalleryproject.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.imagegalleryproject.databinding.FragmentImageViewerBinding
import com.example.imagegalleryproject.model.FavoriteImage
import com.example.imagegalleryproject.viewmodel.FavoriteViewModel


class ImageViewerFragment : Fragment() {
    lateinit var binding: FragmentImageViewerBinding
    private lateinit var favoriteViewModel: FavoriteViewModel
    private val args: ImageViewerFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val onBackPressedCallback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentImageViewerBinding.inflate(inflater, container, false)

        favoriteViewModel = FavoriteViewModel(requireActivity().application)

        (requireActivity() as AppCompatActivity).supportActionBar?.title = "View Image"

        requireActivity().supportFragmentManager

        val imageRes = args.imageRes

        Glide.with(binding.ivDialog).load(imageRes).into(binding.ivDialog)


        binding.addFav.setOnClickListener {
            favoriteViewModel.addFavorites(FavoriteImage(imageRes))
            Toast.makeText(requireContext(), "Image Added to Favorites", Toast.LENGTH_SHORT).show()
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "View Image"
    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "View Image"
    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "View Image"
    }
}