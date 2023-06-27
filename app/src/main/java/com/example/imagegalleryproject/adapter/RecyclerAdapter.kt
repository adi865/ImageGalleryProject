package com.example.imagegalleryproject.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.imagegalleryproject.R
import com.example.imagegalleryproject.databinding.ListItemBinding
import com.example.imagegalleryproject.model.FavoriteImage
import com.example.imagegalleryproject.model.Search

class RecyclerAdapter(val context: Context, private val recyclerItemClickListener: RecyclerItemClickListener?) : RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder>() {
    constructor(context: Context) : this(
        context, null)
    val pathsList: ArrayList<FavoriteImage> = ArrayList<FavoriteImage>()


    private val differCallback = object : DiffUtil.ItemCallback<Search>() {
        override fun areItemsTheSame(oldItem: Search, newItem: Search): Boolean {
            return oldItem.imdbID == newItem.imdbID
        }

        override fun areContentsTheSame(oldItem: Search, newItem: Search): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)


    inner class RecyclerViewHolder(private val binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(search: Search) {
            Glide.with(binding.root.context)
                .load(differ.currentList[position].Poster)
                .placeholder(R.drawable.ic_loading_foreground)
                .into(binding.imgMovie)

            val selectedItems = HashSet<Int>()
            binding.imgMovie.setOnLongClickListener {
                binding.checkbox.visibility = View.VISIBLE
                val itemPosition = adapterPosition
                if (!selectedItems.contains(itemPosition)) {
                    binding.checkbox.visibility = View.VISIBLE
                    selectedItems.add(itemPosition)
                } else {
                    binding.checkbox.visibility = View.GONE
                    selectedItems.remove(itemPosition)
                }

                binding.checkbox.setOnClickListener {
                    val isChecked = binding.checkbox.isChecked

                    if (isChecked) {
                        val selectedPaths = selectedItems.mapNotNull { position ->
                            differ.currentList.getOrNull(position)?.Poster
                        }
                        pathsList.clear()
                        pathsList.addAll(selectedPaths.map { FavoriteImage(it) })
                    } else {
                        pathsList.removeAll { item ->
                            selectedItems.contains(differ.currentList.indexOfFirst { it.Poster == item.favorite })
                        }
                    }
                    recyclerItemClickListener!!.itemClickListener(pathsList)
                }
                recyclerItemClickListener!!.itemLongClickListener()
                return@setOnLongClickListener true
            }

            binding.imgMovie.setOnClickListener {
                if (binding.checkbox.visibility == View.VISIBLE) {
                    binding.checkbox.visibility = View.GONE
                }
                recyclerItemClickListener!!.goToViewFragment(differ.currentList[position].Poster!!)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecyclerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val image = differ.currentList[position]
        holder.bind(image)
    }


    interface RecyclerItemClickListener {
        fun itemClickListener(paths: List<FavoriteImage>)

        fun itemLongClickListener(): Boolean

        fun goToViewFragment(selectedImage: String)
    }
}
