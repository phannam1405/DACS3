package com.example.dacs3.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.dacs3.ui.view.CNMusicFragment
import com.example.dacs3.ui.view.JPMusicFragment
import com.example.dacs3.ui.view.KRFragment
import com.example.dacs3.ui.view.USUKMusicFragment
import com.example.dacs3.ui.view.VNMusicFragment

class ViewPageMusicAdapter(fragManager: FragmentManager, lifecycle: Lifecycle):
    FragmentStateAdapter(fragManager, lifecycle ) {


    override fun getItemCount(): Int {
        return 5
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> VNMusicFragment()
            1 -> CNMusicFragment()
            2 -> JPMusicFragment()
            3 -> USUKMusicFragment()
            else -> KRFragment()
        }
    }
}