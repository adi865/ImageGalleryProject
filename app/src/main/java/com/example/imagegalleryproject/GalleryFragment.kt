package com.example.imagegalleryproject

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.imagegalleryproject.adapter.RecyclerAdapter
import com.example.imagegalleryproject.databinding.FragmentGalleryBinding
import com.example.imagegalleryproject.db.*
import com.example.imagegalleryproject.model.FavoriteImage
import com.example.imagegalleryproject.viewmodel.FavoriteViewModel
import com.example.imagegalleryproject.viewmodel.FavoriteViewModelFactory
import com.example.imagegalleryproject.viewmodel.ImageViewModel
import com.example.imagegalleryproject.viewmodel.ImageViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class GalleryFragment: Fragment(), RecyclerAdapter.RecyclerItemClickListener {
    private var binding: FragmentGalleryBinding? = null
    private lateinit var recyclerAdapter: RecyclerAdapter
    private lateinit var viewModel: ImageViewModel
    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var factory: ImageViewModelFactory
    private lateinit var favoriteFactory: FavoriteViewModelFactory
    private lateinit var imageDao: ImageDao
    private lateinit var favoriteDao: FavoriteDao

    private lateinit var posterRepository: PosterRepository


    private val binding1 get() = binding!!

    private lateinit var mAuth: FirebaseAuth

    private var actMode: ActionMode? = null

    private lateinit var imagePathList: ArrayList<String>

    var isInActionMode = false


    private lateinit var inputParamter: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        binding = FragmentGalleryBinding.inflate(inflater, container, false)


        imageDao = DatabaseInstance.getInstance(requireActivity()).imageDao()

        favoriteDao = FavoriteDatabaseInstance.getInstance(requireActivity()).imageDao()

        val db = DatabaseInstance.getInstance(requireContext())
        posterRepository = PosterRepository(db)


        favoriteFactory = FavoriteViewModelFactory(activity?.application!!, favoriteDao)

        favoriteViewModel =
            ViewModelProvider(this, favoriteFactory).get(FavoriteViewModel::class.java)

        imagePathList = ArrayList<String>()

        mAuth = FirebaseAuth.getInstance()

        binding!!.submitBtn.setOnClickListener {
            inputParamter = binding!!.inputParameter.text.toString()
            if(inputParamter != null) {
                factory = ImageViewModelFactory(activity?.application!!, posterRepository, inputParamter)
                viewModel = ViewModelProvider(this, factory).get(ImageViewModel::class.java)
                viewModel.getImages(inputParamter)
                getImagePath()
            } else {
                Toast.makeText(requireActivity(), "Please enter input", Toast.LENGTH_SHORT).show()
            }

        }

        return binding!!.root
    }

    private val actionModeCallback: ActionMode.Callback = object: ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            mode!!.menuInflater.inflate(R.menu.custom_menu, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
           return false
        }

        override fun onActionItemClicked(mode: ActionMode?, menu: MenuItem?): Boolean {
            when(menu!!.itemId) {
                R.id.signOut -> {
                if(mAuth.currentUser != null) {
                    //reminder session not being managed manually, using firebase exclusively
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
                    (activity as AppCompatActivity).supportActionBar?.show()
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
           actMode = null
        }
    }

    fun getImagePath() {
        recyclerAdapter = RecyclerAdapter(requireContext(), this)
        binding1.rv.layoutManager = GridLayoutManager(activity, 4)
        binding1.rv.setHasFixedSize(true)
        binding1.rv.adapter = recyclerAdapter
        viewModel.postersFromDB.observe(viewLifecycleOwner, {
            recyclerAdapter.differ.submitList(it)
//            recyclerAdapter.notifyDataSetChanged() //is an expensive process, as it recreates (refereshes all rows) ViewHolder recycled by the RecyclerView
        })
    }

    override fun removeOnItemLongClickListener(imagePath: String) {
         //to be done in favorite fragment, not here! Will Remove once the project is finalized
//        viewModel.removeImage(Image(imagePath))
    }

    override fun itemClickListener(paths: ArrayList<String>) {
        imagePathList.addAll(paths)
    }

    override fun itemLongClickListener(): Boolean {
        if(actMode != null) {
            return false
        } else {
            isInActionMode = true
            setMenuVisibility(false)
            (activity as AppCompatActivity).supportActionBar?.hide()
            actMode = requireActivity().startActionMode(actionModeCallback)
            return true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        if(actMode != null) {
            actMode!!.finish()
        }
    }
}
