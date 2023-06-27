package com.example.imagegalleryproject.ui.fragments

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.imagegalleryproject.R
import com.example.imagegalleryproject.adapter.FavRecyclerAdapter
import com.example.imagegalleryproject.databinding.FragmentImageBinding
import com.example.imagegalleryproject.ui.SignInActivity
import com.example.imagegalleryproject.util.isVisible
import com.example.imagegalleryproject.viewmodel.FavoriteViewModel
import com.google.firebase.auth.FirebaseAuth

class ImageFragment : Fragment(), FavRecyclerAdapter.FavRecyclerItemClickListener {
    private var _binding: FragmentImageBinding? = null
    private lateinit var adapter: FavRecyclerAdapter
    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var mAuth: FirebaseAuth

    private val binding get() = _binding!!

    private var actMode: ActionMode? = null
    private var isInActionMode: Boolean = false

    private lateinit var favSelectedImages: ArrayList<Int>


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
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentImageBinding.inflate(inflater, container, false)

        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Favorites Gallery"

//        favoriteViewModel = FavoriteViewModel(requireActivity().application)

        favSelectedImages = ArrayList()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        binding.favRv.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        binding.favRv.setHasFixedSize(true)
        adapter = FavRecyclerAdapter(requireContext(), this)
        binding.favRv.adapter = adapter
        Log.i(
            "Favourite Area",
            "---------- I am before staggered ----- Count is :  ${favSelectedImages.count()}"
        )
//        favoriteViewModel.getAllFavorites()
//        favoriteViewModel.fetchedImages.observe(viewLifecycleOwner) {
//            adapter.differ.submitList(it)
//            if (adapter.differ.currentList.size < 0) {
//                showEmpty(true)
//            } else {
//                showEmpty(false)
//            }
//        }
    }

    private val actionModeCallback: ActionMode.Callback = object: ActionMode.Callback {
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
                    //alert dialog goes here
                    val builder: AlertDialog.Builder = AlertDialog.Builder(
                        ContextThemeWrapper(
                            requireContext(),
                            R.style.AlertDialogCustom
                        )
                    )
                    builder.setTitle("Confirm Deletion?")
                    builder.setInverseBackgroundForced(true)
                    builder.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
//                        favoriteViewModel.deleteFavorites(favSelectedImages)
                        dialog.dismiss()
                    })

                    builder.setNegativeButton("Cancel",
                        DialogInterface.OnClickListener { dialog, _ ->
                            dialog.dismiss()
                        })
                    builder.create().show()
                    mode?.finish()
//                    if (adapter.itemCount <= 1) {
//                        _binding!!.tvDefault1.visibility = View.VISIBLE
//                        _binding!!.favRv.visibility = View.GONE
//                    }
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
        _binding = null
    }

    override fun itemClickListener(paths: ArrayList<Int>) {
        paths.forEach {
            favSelectedImages.add(it)
        }
    }

    override fun itemLongClickListener(): Boolean {
        return if (actMode != null) {
            false
        } else {
            isInActionMode = true
            setMenuVisibility(false)
            (activity as AppCompatActivity).supportActionBar?.hide()
            actMode = requireActivity().startActionMode(actionModeCallback)
            true
        }
    }

    override fun goToViewFragmen(imagePath: String) {
        val directions =
            ImageFragmentDirections.actionImageFragmentToImageViewerFragment(imagePath)
        findNavController().navigate(directions)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        if (actMode != null) {
            actMode!!.finish()
        }
    }

    private fun showEmpty(isShown: Boolean) {
        binding.apply {
            if (isShown) {
                emptyBody.isVisible(true, listBody)
            } else {
                emptyBody.isVisible(false, listBody)
            }
        }
    }
}