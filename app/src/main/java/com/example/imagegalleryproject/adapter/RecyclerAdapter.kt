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

class RecyclerAdapter(val context: Context, private val recyclerItemClickListener: RecyclerItemClickListener) :
    RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder>() {

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


    inner class RecyclerViewHolder(val binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var selected = BooleanArray(differ.currentList.size)
        var isChecked: Boolean = false
        fun bind(search: Search) {
                Glide.with(binding.rvIv).load(search.Poster)
                    .placeholder(R.drawable.ic_loading_foreground).into(binding.rvIv)

//                if(!mainActivity.isInAction) {
////                    binding.iButton.visibility = View.GONE
//                    binding.checkbox.visibility  = View.GONE
//                } else {
////                    binding.iButton.visibility = View.VISIBLE
//                    binding.checkbox.visibility = View.VISIBLE
//                }

//                if(selected[position]) {
//                    binding.iButton.setImageResource(R.drawable.ic_fav_filled)
//                    binding.iButton.visibility = View.VISIBLE
//                } else {
//                    binding.iButton.setImageResource(R.drawable.ic_fav_unfilled)
//                    binding.iButton.visibility = View.GONE
//                }


                binding.root.setOnLongClickListener {
                    binding.checkbox.visibility = View.VISIBLE
                    return@setOnLongClickListener true
                }


                binding.rvIv.setOnClickListener {
                    if(selected[position]) {
                        binding.checkbox.visibility = View.GONE
                        recyclerItemClickListener.removeOnItemLongClickListener(search.Poster)
                        selected[position] = false
                    } else {
                        binding.checkbox.visibility = View.VISIBLE
                        selected[position] = true
                        binding.checkbox.setOnClickListener {
                            if(binding.checkbox.isChecked) {
                                pathsList.add(differ.currentList.get(position).Poster)
                                recyclerItemClickListener.itemClickListener(pathsList)
                            }
                        }
                        recyclerItemClickListener.itemLongClickListener()
                    }
                }

                binding.root.setOnClickListener {
//                    recyclerItemClickListener.itemClickListener(image)
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

    interface RecyclerItemClickListener {
        public fun itemClickListener(paths: ArrayList<String>)

        public fun itemLongClickListener(): Boolean

        public fun removeOnItemLongClickListener(imagePath: String)
    }
}
