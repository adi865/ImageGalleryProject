package com.example.imagegalleryproject.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.imagegalleryproject.databinding.FavListItemBinding
import com.example.imagegalleryproject.model.FavoriteImage
import com.example.imagegalleryproject.model.Image

class FavRecyclerAdapter(): RecyclerView.Adapter<FavRecyclerAdapter.FavViewHolder>() {


    private val differCallback = object : DiffUtil.ItemCallback<FavoriteImage>() {
        override fun areItemsTheSame(oldItem: FavoriteImage, newItem: FavoriteImage): Boolean {
            return oldItem.favorite== newItem.favorite
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
        fun bind(favoriteImage: FavoriteImage) {
            Glide.with(binding.favRvIv).load(favoriteImage.favorite).into(binding.favRvIv)
        }
    }
}