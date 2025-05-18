package com.example.dacs3.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dacs3.databinding.CustomSingerListBinding

class SingerListAdapter(private var singerImages: List<String>) :
    RecyclerView.Adapter<SingerListAdapter.SingerViewHolder>() {

    var onItemClick: ((String) -> Unit)? = null

    inner class SingerViewHolder(val binding: CustomSingerListBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingerViewHolder {
        val binding = CustomSingerListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SingerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SingerViewHolder, position: Int) {
        val imageUrl = singerImages[position]
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .into(holder.binding.imgSong)

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(imageUrl)
        }
    }

    override fun getItemCount(): Int = singerImages.size
}