package com.example.imagegalleryproject.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.imagegalleryproject.ImageFragment
import com.example.imagegalleryproject.R
import com.example.imagegalleryproject.databinding.ListItemBinding
import com.example.imagegalleryproject.model.Image
import com.squareup.picasso.Picasso
import java.io.File

class RecyclerAdapter(val context: Context, private val clickListener: (Image) -> Unit) :
    RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<Image>() {
        override fun areItemsTheSame(oldItem: Image, newItem: Image): Boolean {
            return oldItem.path == newItem.path
        }

        override fun areContentsTheSame(oldItem: Image, newItem: Image): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    val imagePathList: ArrayList<String> = ArrayList<String>()

    inner class RecyclerViewHolder(val binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(image: Image) {
            val file = File(image.path)
            if (file.exists()) {
                Glide.with(binding.rvIv).load(image.path)
                    .placeholder(R.drawable.ic_launcher_background).into(binding.rvIv)

                binding.root.setOnClickListener {
                    clickListener(image)
                }
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
        val image = differ.currentList.get(position)
        holder.bind(image)
    }

}
