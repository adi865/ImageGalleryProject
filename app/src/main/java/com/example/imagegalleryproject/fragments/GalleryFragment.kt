package com.example.imagegalleryproject.fragments

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.invalidateOptionsMenu
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.imagegalleryproject.R
import com.example.imagegalleryproject.SignInActivity
import com.example.imagegalleryproject.adapter.RecyclerAdapter
import com.example.imagegalleryproject.databinding.FragmentGalleryBinding
import com.example.imagegalleryproject.db.*
import com.example.imagegalleryproject.model.FavoriteImage
import com.example.imagegalleryproject.viewmodel.FavoriteViewModel
import com.example.imagegalleryproject.viewmodel.ImageViewModel
import com.example.imagegalleryproject.viewmodel.LocalImageViewModel
import com.google.firebase.auth.FirebaseAuth

class GalleryFragment: Fragment(), RecyclerAdapter.RecyclerItemClickListener {
    private var binding: FragmentGalleryBinding? = null
    private var recyclerAdapter: RecyclerAdapter? = null
    private lateinit var viewModel: ImageViewModel
    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var localImageViewModel: LocalImageViewModel

    private lateinit var imageDao: ImageDao
    private lateinit var favoriteDao: FavoriteDao

    private lateinit var posterRepository: PosterRepository

    private val binding1 get() = binding!!

    private lateinit var mAuth: FirebaseAuth

    private var actMode: ActionMode? = null

    private lateinit var imagePathList: ArrayList<String>

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Movie Gallery"
        invalidateOptionsMenu(requireActivity())
        binding1.tvDefault.visibility = View.GONE
    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        invalidateOptionsMenu(requireActivity())
    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Movie Gallery"
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
        binding = FragmentGalleryBinding.inflate(inflater, container, false)

        //it is called before onCreateOptionsMenu
        setHasOptionsMenu(true)

        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Movie Gallery"
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // The usage of an interface lets you inject your own implementation
        val menuHost: MenuHost = requireActivity()

        // Add menu items without using the Fragment Menu APIs
        // Note how we can tie the MenuProvider to the viewLifecycleOwner
        // and an optional Lifecycle.State (here, RESUMED) to indicate when
        // the menu should be visible
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.toolbar_gallery, menu)
                val search = menu.findItem(R.id.appSearchBar)
                val searchView = search.actionView as androidx.appcompat.widget.SearchView
                searchView.queryHint = "Search Images from API"
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                    androidx.appcompat.widget.SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        viewModel = ImageViewModel(requireActivity().application, posterRepository, query)
                        viewModel.getImages(query)
                        binding1.progressBar.visibility = View.VISIBLE
                        binding1.tvDefault.visibility = View.GONE
                        binding1.rv.visibility = View.VISIBLE
                        launchRecyclerView()
                        return true
                    }
                    override fun onQueryTextChange(newText: String?): Boolean {
                        return false
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection

                return false
            }
        }, viewLifecycleOwner)

        imageDao = DatabaseInstance.getInstance(requireActivity()).imageDao()

        favoriteDao = FavoriteDatabaseInstance.getInstance(requireActivity()).imageDao()

        posterRepository = PosterRepository()

        favoriteViewModel = FavoriteViewModel(requireActivity().application)

        imagePathList = ArrayList<String>()

        mAuth = FirebaseAuth.getInstance()

        localImageViewModel = LocalImageViewModel(requireActivity().application, posterRepository)

        recyclerAdapter = RecyclerAdapter(requireContext(), this)
        binding!!.rv.adapter = recyclerAdapter
        binding!!.rv.layoutManager = GridLayoutManager(activity, 4)
        binding!!.rv.setHasFixedSize(true)
        localImageViewModel.localDBImages.observe(viewLifecycleOwner,  {
            recyclerAdapter!!.differ.submitList(it)
            if(recyclerAdapter!!.differ.currentList.size < 1) {
                binding!!.rv.visibility = View.GONE
                binding!!.tvDefault.visibility = View.VISIBLE
            } else {
                binding!!.rv.visibility = View.VISIBLE
                binding!!.tvDefault.visibility = View.GONE
            }
        })


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
                        //reminder: session not being managed manually, using firebase exclusively
                        mAuth.signOut()
                        val intent = Intent(requireActivity(), SignInActivity::class.java)
                        startActivity(intent)
                    }
                }
                R.id.addFav -> {
                    imagePathList.forEach {
                        favoriteViewModel.addFavorites(FavoriteImage(it))
                    }
                    mode?.finish()
                    (requireActivity() as AppCompatActivity).supportActionBar?.show()
                    requireActivity().invalidateOptionsMenu()
                    onResume()
                }
                R.id.cancel -> {
                    mode?.finish()
                    (requireActivity() as AppCompatActivity).supportActionBar?.show()
                    invalidateOptionsMenu(requireActivity())
                }
                else -> {
                    return false
                }
            }
            return true
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            (requireActivity() as AppCompatActivity).supportActionBar?.show()
            invalidateOptionsMenu(requireActivity())
            actMode = null
        }
    }

    fun launchRecyclerView() {
        recyclerAdapter = RecyclerAdapter(requireContext(), this)
        binding1.rv.layoutManager = GridLayoutManager(activity, 4)
        binding1.rv.setHasFixedSize(true)
        binding1.rv.adapter = recyclerAdapter
        viewModel.postersFromDB.observe(viewLifecycleOwner, {
            recyclerAdapter!!.setData(it)
            recyclerAdapter!!.notifyDataSetChanged()
            binding!!.progressBar.visibility = View.GONE
//            recyclerAdapter.notifyDataSetChanged() //is an expensive process, as it recreates (refereshes all rows) ViewHolder recycled by the RecyclerView
        })
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.toolbar_gallery, menu)
//        val search = menu.findItem(R.id.appSearchBar)
//        val searchView = search.actionView as androidx.appcompat.widget.SearchView
//        searchView.queryHint = "Search Images from API"
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
//            androidx.appcompat.widget.SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                viewModel = ImageViewModel(requireActivity().application, posterRepository, query)
//                viewModel.getImages(query)
//                binding1.progressBar.visibility = View.VISIBLE
//                binding1.tvDefault.visibility = View.GONE
//                binding1.rv.visibility = View.VISIBLE
//                launchRecyclerView()
//                return true
//            }
//            override fun onQueryTextChange(newText: String?): Boolean {
//                return false
//            }
//        })
//        super.onCreateOptionsMenu(menu, inflater)
//    }

    override fun itemClickListener(paths: ArrayList<String>) {
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
        val direction = GalleryFragmentDirections.actionGalleryFragmentToImageViewerFragment(selectedImage)
        findNavController().navigate(direction)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        if (actMode != null) {
            actMode!!.finish()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        binding!!.rv.visibility = View.VISIBLE
        (requireActivity() as AppCompatActivity).invalidateOptionsMenu()
    }
}