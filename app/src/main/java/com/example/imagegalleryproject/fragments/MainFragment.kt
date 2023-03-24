package com.example.imagegalleryproject.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.example.imagegalleryproject.R
import com.example.imagegalleryproject.databinding.FragmentMainBinding

class MainFragment : Fragment() {
    private var binding: FragmentMainBinding? = null

    private val binding1 get() = binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Dashboard"
    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Dashboard"
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(inflater, container, false)

        container!!.removeAllViews()

        binding1.galleryBtn.setOnClickListener {
            var fragment: Fragment? = null
            val fragmentClass: Class<*> = GalleryFragment::class.java

            try {
                fragment = fragmentClass.newInstance() as Fragment
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment!!).commit()
        }


        binding1.favBtn.setOnClickListener {
            var fragment: Fragment? = null
            val fragmentClass: Class<*> = ImageFragment::class.java

            try {
                fragment = fragmentClass.newInstance() as Fragment
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment!!).commit()
        }

        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Dashboard"

        binding1.bottomNavigationView.setOnItemSelectedListener{ item: MenuItem ->
            val itemId = item.itemId
            if (itemId == R.id.gallery) {
                var fragment: Fragment? = null
                val fragmentClass: Class<*> = GalleryFragment::class.java

                try {
                    fragment = fragmentClass.newInstance() as Fragment
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                val fragmentManager = requireActivity().supportFragmentManager
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment!!).commit()
            } else if (itemId == R.id.fav) {
                var fragment: Fragment? = null
                val fragmentClass: Class<*> = ImageFragment::class.java

                try {
                    fragment = fragmentClass.newInstance() as Fragment
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                val fragmentManager = requireActivity().supportFragmentManager
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment!!).commit()
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