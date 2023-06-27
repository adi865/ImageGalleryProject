package com.example.imagegalleryproject.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.imagegalleryproject.R
import com.example.imagegalleryproject.databinding.FavListItemBinding
import com.example.imagegalleryproject.model.FavoriteImage

class FavRecyclerAdapter(private val context: Context, val favRecyclerItemClickListener: FavRecyclerItemClickListener): RecyclerView.Adapter<FavRecyclerAdapter.FavViewHolder>() {
    private var favSelectedImages: ArrayList<FavoriteImage> = ArrayList()

    private var selectedImagePositions: ArrayList<Int> = ArrayList()

    private val differCallback = object : DiffUtil.ItemCallback<FavoriteImage>() {
        override fun areItemsTheSame(oldItem: FavoriteImage, newItem: FavoriteImage): Boolean {
            return oldItem.favorite == newItem.favorite
        }

        override fun areContentsTheSame(oldItem: FavoriteImage, newItem: FavoriteImage): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
       val binding = FavListItemBinding.inflate(
           LayoutInflater.from(parent.context),
           parent,
           false)
        return FavViewHolder(binding)
    }

     override fun getItemCount(): Int {
        return differ.currentList.size
    }


    override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
        val favoriteImage = differ.currentList.get(position)
        holder.bind(favoriteImage)
    }


    inner class FavViewHolder(val binding: FavListItemBinding): RecyclerView.ViewHolder(binding.root) {
        var selected = BooleanArray(differ.currentList.size)
        @SuppressLint("SuspiciousIndentation")
        fun bind(favoriteImage: FavoriteImage) {
            Glide.with(binding.root.context)
                .load(favoriteImage.favorite)
                .placeholder(R.drawable.checkbox_drawable)
                .into(binding.favRvIv)

            binding.favRvIv.setOnClickListener {
                favRecyclerItemClickListener.goToViewFragmen(differ.currentList[position].favorite!!)
            }

            binding.favRvIv.setOnLongClickListener {
                binding.checkbox.visibility = View.VISIBLE
                if(selected[position]) {
                    binding.checkbox.visibility = View.GONE
                    selected[position] = false
                } else {
                    binding.checkbox.visibility = View.VISIBLE
                    selected[position] = true
                    binding.checkbox.setOnClickListener {
                        if(binding.checkbox.isChecked) {
                            favSelectedImages.add(FavoriteImage(differ.currentList[position].favorite!!))
                            val position = differ.currentList.indexOf(differ.currentList[position])
                            selectedImagePositions.add(position)
                            favRecyclerItemClickListener.itemClickListener(selectedImagePositions)
                        }
                    }
                    favRecyclerItemClickListener.itemLongClickListener()
                }
                return@setOnLongClickListener true
            }
        }
    }

    interface FavRecyclerItemClickListener {
        public fun itemClickListener(paths: ArrayList<Int>)

        public fun itemLongClickListener(): Boolean

        public fun goToViewFragmen(imagePath: String)
    }
}