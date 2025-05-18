package com.example.dacs3.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.dacs3.ui.view.MusicGenreFragment

class ViewPageMusicAdapter(
    fragManager: FragmentManager,
    lifecycle: Lifecycle,
    private val categories: List<String>
) : FragmentStateAdapter(fragManager, lifecycle) {

    override fun getItemCount(): Int = categories.size

    override fun createFragment(position: Int): Fragment {
        return MusicGenreFragment.newInstance(categories[position])
    }
}