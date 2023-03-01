package com.example.imagegalleryproject.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.imagegalleryproject.DisplayImageActivity
import com.example.imagegalleryproject.R
import com.example.imagegalleryproject.databinding.ListItemBinding
import com.example.imagegalleryproject.model.Image
import com.squareup.picasso.Picasso
import java.io.File

class RecyclerAdapter(val context: Context): ListAdapter<Image, RecyclerAdapter.RecyclerViewHolder>(RecyclerComparator()) {


    inner class RecyclerViewHolder(private val binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(image: Image) {
            val file = File(image.path)
            if (file.exists()) {
                Picasso.get().load(file).placeholder(R.drawable.ic_launcher_background)
                    .into(binding.rvIv)
                binding.root.setOnClickListener {
                    val intent = Intent(context, DisplayImageActivity::class.java)
                    intent.putExtra("img_path", image.path)
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecyclerViewHolder(binding)
    }

//    override fun getItemCount(): Int {
//        return imagePathList.size
//    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
//       with(holder.binding) {
//           val file = File(imagePathList[position])
//           if(file.exists()) {
//               Picasso.get().load(file).placeholder(R.drawable.ic_launcher_background).into(rvIv)
//               root.setOnClickListener {
//                   val intent = Intent(context, DisplayImageActivity::class.java)
//                   intent.putExtra("img_path", imagePathList.get(position))
//                   context.startActivity(intent)
//               }
//           }
//       }
    }
}

class RecyclerComparator : DiffUtil.ItemCallback<Image>() {
    override fun areItemsTheSame(oldItem: Image, newItem: Image): Boolean {
        return oldItem.path == newItem.path
    }

    override fun areContentsTheSame(oldItem: Image, newItem: Image): Boolean {
        return oldItem == newItem
    }
}