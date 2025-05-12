package com.example.dacs3.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPageMusicAdapter(
    fragManager: FragmentManager,
    lifecycle: Lifecycle,
    private val categories: List<String> // Nhận categories từ bên ngoài
) : FragmentStateAdapter(fragManager, lifecycle) {

    override fun getItemCount(): Int = categories.size

    override fun createFragment(position: Int): Fragment {
        return MusicGenreFragment.newInstance(categories[position])
    }
}