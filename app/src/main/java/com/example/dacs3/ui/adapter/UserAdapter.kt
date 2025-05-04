package com.example.dacs3.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dacs3.R
import com.example.dacs3.data.model.DataUser
import com.example.dacs3.databinding.CustomUserBinding

class UserAdapter(
    private val onItemClick: (DataUser) -> Unit,
    private val onFollowClick: (DataUser) -> Unit
) : RecyclerView.Adapter<UserAdapter.SearchUserViewHolder>() {

    private var users = emptyList<DataUser>()

    inner class SearchUserViewHolder(private val binding: CustomUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(users: DataUser) {
            binding.apply {
                txtName.text = users.userName
                txtPN.text = users.phoneNumber
                txtEMmail.text = users.email

                Glide.with(itemView.context)
                    .load(users.avatarUrl.takeIf { !it.isNullOrEmpty() } ?: R.drawable.icon_avatar)
                    .circleCrop()
                    .into(imgUser)

                root.setOnClickListener { onItemClick(users) }

                btnFollow.setOnClickListener { onFollowClick(users) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchUserViewHolder {
        val binding = CustomUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SearchUserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchUserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int = users.size

    fun updateData(newSongs: List<DataUser>) {
        users = newSongs
        notifyDataSetChanged()
    }
}
