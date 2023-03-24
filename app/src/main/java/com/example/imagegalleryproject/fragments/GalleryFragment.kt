package com.example.imagegalleryproject.fragments

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.SearchView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.imagegalleryproject.MainActivity
import com.example.imagegalleryproject.R
import com.example.imagegalleryproject.SignInActivity
import com.example.imagegalleryproject.adapter.RecyclerAdapter
import com.example.imagegalleryproject.databinding.FragmentGalleryBinding
import com.example.imagegalleryproject.db.*
import com.example.imagegalleryproject.model.FavoriteImage
import com.example.imagegalleryproject.viewmodel.FavoriteViewModel
import com.example.imagegalleryproject.viewmodel.ImageViewModel
import com.example.imagegalleryproject.viewmodel.ViewerViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import okhttp3.internal.notifyAll

class GalleryFragment: Fragment(), RecyclerAdapter.RecyclerItemClickListener {
    private var binding: FragmentGalleryBinding? = null
    private var recyclerAdapter: RecyclerAdapter? = null
    private lateinit var viewModel: ImageViewModel
    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var viewerViewModel: ViewerViewModel

    private lateinit var imageDao: ImageDao
    private lateinit var favoriteDao: FavoriteDao

    private lateinit var posterRepository: PosterRepository


    private val binding1 get() = binding!!

    private lateinit var mAuth: FirebaseAuth

    private var actMode: ActionMode? = null

    private lateinit var imagePathList: ArrayList<String>

    var isInActionMode = false

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Movie Gallery"
        binding!!.rv.visibility = View.VISIBLE
    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Movie Gallery"
    }

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
        binding = FragmentGalleryBinding.inflate(inflater, container, false)

        container!!.removeAllViews()

        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Movie Gallery"

        //it is called before onCreateOptionsMenu
        setHasOptionsMenu(true)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageDao = DatabaseInstance.getInstance(requireActivity()).imageDao()

        favoriteDao = FavoriteDatabaseInstance.getInstance(requireActivity()).imageDao()

        posterRepository = PosterRepository()

        favoriteViewModel = FavoriteViewModel(requireActivity().application)

        viewerViewModel = ViewModelProvider(requireActivity())[ViewerViewModel::class.java]

        imagePathList = ArrayList<String>()

        mAuth = FirebaseAuth.getInstance()

        binding!!.rv.visibility = View.VISIBLE
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
        recyclerAdapter = RecyclerAdapter(requireContext(), this)
        binding1.tvDefault.visibility = View.GONE
        binding1.rv.layoutManager = GridLayoutManager(activity, 4)
        binding1.rv.setHasFixedSize(true)
        binding1.rv.adapter = recyclerAdapter
        viewModel.postersFromDB.observe(viewLifecycleOwner, {
            recyclerAdapter!!.setData(it)
            recyclerAdapter!!.notifyDataSetChanged()
//            recyclerAdapter.notifyDataSetChanged() //is an expensive process, as it recreates (refereshes all rows) ViewHolder recycled by the RecyclerView
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_gallery, menu)
        val search = menu.findItem(R.id.appSearchBar)
        val searchView = search.actionView as SearchView
        search.setIcon(R.drawable.ic_search)
        searchView.imeOptions = EditorInfo.IME_ACTION_SEARCH
        searchView.isHardwareAccelerated
        searchView.queryHint = "Search Images from API"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel = ImageViewModel(requireActivity().application, posterRepository, query)
                viewModel.getImages(query)
                launchRecyclerView()
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    override fun itemClickListener(paths: ArrayList<String>) {
        imagePathList.addAll(paths)
    }

    override fun itemLongClickListener(): Boolean {
        if (actMode != null) {
            return false
        } else {
            isInActionMode = true
            setMenuVisibility(false)
            (requireActivity() as AppCompatActivity).supportActionBar?.hide()
            actMode = requireActivity().startActionMode(actionModeCallback)
            return true
        }
    }

    override fun goToViewFragment(selectedImage: String) {
        val imageViewerFragment = ImageViewerFragment()
        val fragmentManager = requireActivity().supportFragmentManager
        viewerViewModel.fetchImage(selectedImage)
        fragmentManager.beginTransaction().replace(R.id.flContent, imageViewerFragment).addToBackStack(null).commit()
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
    }

}