package com.example.dacs3.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dacs3.databinding.CustomCarouselBinding
class CarouselAdapter(private val imageList: MutableList<Int>) :
    RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder>(){

    inner class CarouselViewHolder(private val binding: CustomCarouselBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(image : Int){
            binding.itemCarousel.setImageResource(image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        return CarouselViewHolder(CustomCarouselBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        holder.bind(imageList[position])
    }

}