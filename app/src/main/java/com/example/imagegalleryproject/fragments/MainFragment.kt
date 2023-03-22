package com.example.imagegalleryproject.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.example.imagegalleryproject.R
import com.example.imagegalleryproject.databinding.FragmentMainBinding

class MainFragment : Fragment() {
    private var binding: FragmentMainBinding? = null
    private lateinit var navController: NavController

    private val binding1 get() = binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        navController = Navigation.findNavController(requireActivity(), R.id.imageNavHostContainer)
    }

    override fun onStart() {
        super.onStart()
        navController = requireActivity().findNavController(R.id.imageNavHostContainer)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(inflater, container, false)


        binding1.galleryBtn.setOnClickListener {
                navController.navigate(R.id.action_mainFragment_to_galleryFragment)
        }


        binding1.favBtn.setOnClickListener {
            binding1.root.findNavController().navigate(R.id.action_mainFragment_to_imageFragment)
        }

        return binding1.root
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}