package com.example.dacs3.ui.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ListView
import androidx.lifecycle.ViewModelProvider
import com.example.dacs3.R
import com.example.dacs3.data.model.DataSongList
import com.example.dacs3.ui.adapter.SongGerneAdapter
import com.example.dacs3.ui.viewmodel.MainViewModel

class MusicGenreFragment : Fragment(R.layout.fragment_music_genre) {
    private lateinit var listView: ListView
    private lateinit var adapter: SongGerneAdapter
    private lateinit var viewModel: MainViewModel
    private var category: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        category = arguments?.getString("category")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView = view.findViewById(R.id.lv_music)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        category?.let {
            viewModel.getSongsByCategory(it).observe(viewLifecycleOwner) { songs ->
                adapter = SongGerneAdapter(requireContext(), songs)
                listView.adapter = adapter

                adapter.setOnItemClickListener(object : SongGerneAdapter.OnItemClickListener {
                    override fun onItemClick(song: DataSongList) {
                        val intent = Intent(requireContext(), PlayerActivity::class.java).apply {
                            putExtra("song", song)
                            putExtra("song_list", ArrayList(songs))
                        }
                        startActivity(intent)
                    }
                })
            }
        }
    }

    companion object {
        fun newInstance(category: String): MusicGenreFragment {
            val fragment = MusicGenreFragment()
            val args = Bundle()
            args.putString("category", category)
            fragment.arguments = args
            return fragment
        }
    }
}