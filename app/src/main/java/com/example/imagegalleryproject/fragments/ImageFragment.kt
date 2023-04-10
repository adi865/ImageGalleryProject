package com.example.imagegalleryproject.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.imagegalleryproject.R
import com.example.imagegalleryproject.SignInActivity
import com.example.imagegalleryproject.adapter.FavRecyclerAdapter
import com.example.imagegalleryproject.databinding.FragmentImageBinding
import com.example.imagegalleryproject.db.FavoriteDao
import com.example.imagegalleryproject.db.FavoriteDatabaseInstance
import com.example.imagegalleryproject.model.FavoriteImage
import com.example.imagegalleryproject.util.DataStatus
import com.example.imagegalleryproject.util.isVisible
import com.example.imagegalleryproject.viewmodel.FavoriteViewModel
import com.google.firebase.auth.FirebaseAuth

class ImageFragment: Fragment(), FavRecyclerAdapter.FavRecyclerItemClickListener {
    private var binding: FragmentImageBinding? = null
    private lateinit var adapter: FavRecyclerAdapter
    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var favoriteDao: FavoriteDao

    private lateinit var mAuth: FirebaseAuth

    private val binding1 get() = binding!!

    private var actMode: ActionMode? = null
    private var isInActionMode: Boolean = false

    private lateinit var favSelectedImages: ArrayList<String>


    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Favorites Gallery"
    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Favorites Gallery"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentImageBinding.inflate(inflater, container, false)

        favoriteDao = FavoriteDatabaseInstance.getInstance(requireActivity()).imageDao()

        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Favorites Gallery"

        favoriteViewModel =
            FavoriteViewModel(requireActivity().application)

        favSelectedImages = ArrayList()

        mAuth = FirebaseAuth.getInstance()

        favoriteViewModel.getAllFavorites()
        favoriteViewModel.fetchedImages.observe(viewLifecycleOwner, {
            when(it.status) {
                DataStatus.Status.LOADING -> {
                    binding1.emptyBody.isVisible(false, binding1.favRv)
                }
                DataStatus.Status.ERROR -> {
                    Toast.makeText(requireContext(), it.msg, Toast.LENGTH_SHORT).show()
                }
                DataStatus.Status.SUCCESS -> {
                    it.isEmpty?.let {
                        showEmpty(it)
                    }
                    println("from inside coroutine observer")
                    binding1.favRv.layoutManager = GridLayoutManager(requireContext(), 4)
                    binding1.favRv.setHasFixedSize(true)
                    adapter = FavRecyclerAdapter(requireContext(), this)
                    binding1.favRv.adapter = adapter
                    adapter.differ.submitList(it.data)
                }
            }
        })
        return binding1.root
    }
    private val actionModeCallback: ActionMode.Callback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            mode!!.menuInflater.inflate(R.menu.fav_custom_menu, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode?, menu: MenuItem?): Boolean {
            when (menu!!.itemId) {
                R.id.signOut -> {
                    if (mAuth.currentUser != null) {
                        //reminder: session not being managed manually, using firebase exclusively
                        mAuth.signOut()
                        val intent = Intent(requireActivity(), SignInActivity::class.java)
                        startActivity(intent)
                    }
                }
                R.id.delete -> {
                    favSelectedImages.forEach {
                        favoriteViewModel.deleteFavorites(FavoriteImage(it))
                    }
                    mode?.finish()
                    println("recycler size from CAM ${adapter.itemCount}")
                    if(adapter.itemCount <= 1) {
                        binding1.tvDefault1.visibility = View.VISIBLE
                        binding1.favRv.visibility = View.GONE
                    }
                }
                R.id.cancel -> {
                    mode?.finish()
                    (activity as AppCompatActivity).supportActionBar?.show()
                }
                else -> {
                    return false
                }
            }
            return true
        }

    override fun onDestroyActionMode(mode: ActionMode?) {
        (activity as AppCompatActivity).supportActionBar?.show()
        actMode = null
    }
}


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun itemClickListener(paths: ArrayList<String>) {
        favSelectedImages.addAll(paths)
    }

    override fun itemLongClickListener(): Boolean {
        if (actMode != null) {
            return false
        } else {
            isInActionMode = true
            setMenuVisibility(false)
            (activity as AppCompatActivity).supportActionBar?.hide()
            actMode = requireActivity().startActionMode(actionModeCallback)
            return true
        }
    }

    override fun goToViewFragmen(selectedImage: String) {
        val directions = ImageFragmentDirections.actionImageFragmentToImageViewerFragment(selectedImage)
        findNavController().navigate(directions)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        if (actMode != null) {
            actMode!!.finish()
        }
    }

    fun showEmpty(isShown: Boolean) {
        binding!!.apply {
            if(isShown) {
                emptyBody.isVisible(true, listBody)
            } else {
                emptyBody.isVisible(false, listBody)
            }
        }
    }
}