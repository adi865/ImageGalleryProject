package com.example.imagegalleryproject.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.imagegalleryproject.R
import com.example.imagegalleryproject.databinding.FragmentMainBinding

class MainFragment : Fragment() {
    private var binding: FragmentMainBinding? = null

    private val binding1 get() = binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val onBackPressedCallback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                //for quitting app from the Main Fragment when the back button is pressed
                val count = requireActivity().supportFragmentManager.backStackEntryCount
                if (count == 0) {
                    requireActivity().finish()
                } else {
                    findNavController().popBackStack()
                }
            }
        }
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback)
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Dashboard"
    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Dashboard"
    }

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(inflater, container, false)

        binding1.galleryBtn.setOnClickListener {
            val direction = MainFragmentDirections.actionMainFragmentToGalleryFragment()
            findNavController().navigate(direction)
        }


        binding1.favBtn.setOnClickListener {
            val direction = MainFragmentDirections.actionMainFragmentToImageFragment()
            findNavController().navigate(direction)
        }

        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Dashboard"

        binding1.bottomNavigationView.setBackgroundColor(com.google.android.material.R.color.design_default_color_primary)
        binding1.bottomNavigationView.setOnItemSelectedListener{ item: MenuItem ->
            val itemId = item.itemId
            if (itemId == R.id.gallery) {
                val direction = MainFragmentDirections.actionMainFragmentToGalleryFragment()
                findNavController().navigate(direction)
            } else if (itemId == R.id.fav) {
                val direction = MainFragmentDirections.actionMainFragmentToImageFragment()
                findNavController().navigate(direction)
            }
            true
        }
        return binding1.root
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}