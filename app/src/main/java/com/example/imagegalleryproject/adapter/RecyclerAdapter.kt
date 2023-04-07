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
import com.example.imagegalleryproject.model.Search

class RecyclerAdapter(val context: Context, private val recyclerItemClickListener: RecyclerItemClickListener): RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder>() {

    private val pathsList: ArrayList<String> = ArrayList<String>()



    private val differCallback = object : DiffUtil.ItemCallback<Search>() {
        override fun areItemsTheSame(oldItem: Search, newItem: Search): Boolean {
            return oldItem.imdbID == newItem.imdbID
        }

        override fun areContentsTheSame(oldItem: Search, newItem: Search): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    fun setData(imagesList: List<Search>) = differ.submitList(imagesList)

    inner class RecyclerViewHolder(val binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var selected = false
        fun bind(search: Search) {
            Glide.with(binding.imgMovie).load(differ.currentList[position].Poster)
                .placeholder(R.drawable.ic_loading_foreground).into(binding.imgMovie)

            binding.tvMovieName.setText(differ.currentList[position].Title)

            binding.type.setText(differ.currentList[position].Type)

            binding.year.setText(differ.currentList[position].Year)

            binding.imgMovie.setOnLongClickListener {
                binding.checkbox.visibility = View.VISIBLE
                if (selected) {
                    binding.checkbox.visibility = View.GONE
                    selected = false
                } else {
                    binding.checkbox.visibility = View.VISIBLE
                    selected = true
                    binding.checkbox.setOnClickListener {
                        if (binding.checkbox.isChecked) {
                            pathsList.add(differ.currentList[position].Poster)
                            recyclerItemClickListener.itemClickListener(pathsList)
                        }
                    }
                    recyclerItemClickListener.itemLongClickListener()
                }
                return@setOnLongClickListener true
            }

            binding.imgMovie.setOnClickListener {
                if (binding.checkbox.visibility == View.VISIBLE) {
                    binding.checkbox.visibility = View.GONE
                }
                recyclerItemClickListener.goToViewFragment(differ.currentList[position].Poster)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecyclerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.getCurrentList().size
    }


    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val image = differ.currentList.get(position)
        holder.bind(image)
    }

    interface RecyclerItemClickListener {
        public fun itemClickListener(paths: ArrayList<String>)

        public fun itemLongClickListener(): Boolean

        public fun goToViewFragment(selectedImage: String)
    }

}
