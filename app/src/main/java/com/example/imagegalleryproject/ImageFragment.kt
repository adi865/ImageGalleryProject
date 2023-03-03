package com.example.imagegalleryproject

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.example.imagegalleryproject.databinding.FragmentImageBinding
import com.squareup.picasso.Picasso
import java.io.File

class ImageFragment : Fragment() {
    private lateinit var scaleGestureDetector: ScaleGestureDetector //for zooming on the image passed from the recyclerview
    private lateinit var imgPath: String
    private lateinit var binding: FragmentImageBinding

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
        imgPath = requireArguments().getString("img_path")!!
        val thisContext = container!!.context
        scaleGestureDetector = ScaleGestureDetector(thisContext, ScaleListener())



        val imgFile: File = File(imgPath)
        if(imgFile.exists()) {
            Picasso.get().load(imgFile).placeholder(R.drawable.ic_launcher_background).into(binding.idIVImage)
        }
        return binding.root
    }


    private class ScaleListener: ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            return super.onScale(detector)

            var newScaleFactor = detector.scaleFactor
            newScaleFactor = newScaleFactor *  detector.scaleFactor

            return true
        }
    }


}