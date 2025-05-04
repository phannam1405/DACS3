package com.example.dacs3.ui.adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dacs3.data.model.DataSongList
import com.example.dacs3.databinding.CustomSearchBinding

class SearchAdapter(
    private val onItemClick: (DataSongList) -> Unit
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    private var songs = emptyList<DataSongList>()

    inner class SearchViewHolder(private val binding: CustomSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(song: DataSongList) {
            binding.apply {
                txtTitle.text = song.song_name
                txtSinger.text = song.singer_name
                txtTime.text = song.cate

                Glide.with(itemView.context)
                    .load(song.image)
                    .into(imgMusic)

                root.setOnClickListener { onItemClick(song) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = CustomSearchBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(songs[position])
    }

    override fun getItemCount(): Int = songs.size

    fun updateData(newSongs: List<DataSongList>) {
        songs = newSongs
        notifyDataSetChanged()
    }
}
