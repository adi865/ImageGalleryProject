package com.example.imagegalleryproject.fragments

import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.imagegalleryproject.databinding.FragmentImageViewerBinding
import com.example.imagegalleryproject.viewmodel.ViewerViewModel


class ImageViewerFragment : Fragment() {
    lateinit var binding: FragmentImageViewerBinding
    private lateinit var viewerViewModel: ViewerViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentImageViewerBinding.inflate(inflater, container, false)
//        container!!.removeAllViews()

        requireActivity().supportFragmentManager

        viewerViewModel = ViewModelProvider(requireActivity()).get(ViewerViewModel::class.java)

        viewerViewModel.name.observe(viewLifecycleOwner, {
            Glide.with(binding.ivDialog).load(it).into(binding.ivDialog)
        })

        return binding.root
    }

    override fun onResume() {
        super.onResume()
//        viewerViewModel = ViewModelProvider(requireActivity()).get(ViewerViewModel::class.java)
//
//        viewerViewModel.name.observe(viewLifecycleOwner, {
//            Glide.with(binding.ivDialog).load(it).into(binding.ivDialog)
//        })
    }
}