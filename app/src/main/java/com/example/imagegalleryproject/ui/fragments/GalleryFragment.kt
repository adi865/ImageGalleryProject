package com.example.imagegalleryproject.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.imagegalleryproject.R
import com.example.imagegalleryproject.adapter.RecyclerAdapter
import com.example.imagegalleryproject.databinding.FragmentGalleryBinding
import com.example.imagegalleryproject.db.PosterRepository
import com.example.imagegalleryproject.model.FavoriteImage
import com.example.imagegalleryproject.ui.SignInActivity
import com.example.imagegalleryproject.viewmodel.FavoriteViewModel
import com.example.imagegalleryproject.viewmodel.ImageViewModel
import com.example.imagegalleryproject.viewmodel.LocalImageViewModel
import com.google.firebase.auth.FirebaseAuth

class GalleryFragment : Fragment(), RecyclerAdapter.RecyclerItemClickListener {
    private var _binding: FragmentGalleryBinding? = null
    private lateinit var recyclerAdapter: RecyclerAdapter
    private lateinit var viewModel: ImageViewModel

    private lateinit var localImageViewModel: LocalImageViewModel

    private lateinit var favoriteViewModel: FavoriteViewModel


    private lateinit var posterRepository: PosterRepository

    private val binding get() = _binding!!

    private lateinit var mAuth: FirebaseAuth


    private var actMode: ActionMode? = null

    private lateinit var imagePathList: ArrayList<FavoriteImage>

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Movie Gallery"
        Log.i("Gallery Fragment", "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        Log.i("Gallery Fragment", "onPause() called")
    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Movie Gallery"
        Log.i("Gallery Fragment", "onStart() called")
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
        savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Movie Gallery"
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        posterRepository = PosterRepository()
//        favoriteViewModel = FavoriteViewModel(requireActivity().application)
        imagePathList = ArrayList()
        launchRecyclerView()
        mAuth = FirebaseAuth.getInstance()
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_gallery, menu)
        val search = menu.findItem(R.id.appSearchBar)
        val searchView = search.actionView as androidx.appcompat.widget.SearchView
        searchView.queryHint = "Search Images from API"
        searchView.setOnQueryTextListener(object: androidx.appcompat.widget.SearchView.OnQueryTextListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel = ImageViewModel(requireActivity().application, posterRepository, query)
                viewModel.searchResult.observe(viewLifecycleOwner) {
                    binding.rv.layoutManager =
                        StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
                    binding.rv.setHasFixedSize(true)
                    binding.rv.adapter = recyclerAdapter
                    recyclerAdapter.differ.submitList(it)
                    if (it.isNotEmpty()) {
                        binding.listBody.visibility = View.VISIBLE
                        binding.emptyBody.visibility = View.GONE
                    } else {
                        binding.listBody.visibility = View.GONE
                        binding.emptyBody.visibility = View.VISIBLE
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle other menu item selections specific to the GalleryFragment
        when (item.itemId) {
            // Handle other menu items if needed
        }
        return super.onOptionsItemSelected(item)
    }

    private val actionModeCallback: ActionMode.Callback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            mode!!.menuInflater.inflate(R.menu.custom_menu, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode?, menu: MenuItem?): Boolean {
            when (menu!!.itemId) {
                R.id.signOut -> {
                    if (mAuth.currentUser != null) {
                        mAuth.signOut()
                        val intent = Intent(requireActivity(), SignInActivity::class.java)
                        startActivity(intent)
                    }
                }
                R.id.addFav -> {
                    favoriteViewModel.addFavorites(imagePathList)
                    mode?.finish()
                    (requireActivity() as AppCompatActivity).supportActionBar?.show()
                }
                R.id.cancel -> {
                    mode?.finish()
                    (requireActivity() as AppCompatActivity).supportActionBar?.show()
                }
                else -> {
                    return false
                }
            }
            return true
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            (requireActivity() as AppCompatActivity).supportActionBar?.show()
            actMode = null
        }
    }

   fun launchRecyclerView() {
        localImageViewModel = LocalImageViewModel(requireActivity().application)
        recyclerAdapter = RecyclerAdapter(requireContext(), this)
        binding.rv.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        binding.rv.setHasFixedSize(true)
        binding.rv.adapter = recyclerAdapter
        localImageViewModel.getMovieDataforOffline()
        localImageViewModel.localMovieList.observe(viewLifecycleOwner) {
            recyclerAdapter.differ.submitList(it)
            if (it.isNotEmpty()) {
                binding.listBody.visibility = View.VISIBLE
                binding.emptyBody.visibility = View.GONE
            } else {
                binding.listBody.visibility = View.GONE
                binding.emptyBody.visibility = View.VISIBLE
            }
        }
    }

    override fun itemClickListener(paths: List<FavoriteImage>) {
        imagePathList.addAll(paths)
    }

    override fun itemLongClickListener(): Boolean {
        if (actMode != null) {
            return false
        } else {
            setMenuVisibility(false)
            (requireActivity() as AppCompatActivity).supportActionBar?.hide()
            actMode = requireActivity().startActionMode(actionModeCallback)
        }
        return true
    }

    override fun goToViewFragment(selectedImage: String) {
        val direction =
            GalleryFragmentDirections.actionGalleryFragmentToImageViewerFragment(selectedImage)
        findNavController().navigate(direction)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        if (actMode != null) {
            actMode!!.finish()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }
}