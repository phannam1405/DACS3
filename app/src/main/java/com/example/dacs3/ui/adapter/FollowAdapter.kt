package com.example.dacs3.ui.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.dacs3.R
import com.example.dacs3.data.model.DataUser


class FollowAdapter(
    val activity: Activity,
    var list: List<DataUser>
) : ArrayAdapter<DataUser>(activity, R.layout.custom_follow, list) {

    private var itemClickListener: OnItemClickListener? = null
    private var followClickListener: OnFollowClickListener? = null


    private class ViewHolder(view: View) {
        val avatar: ImageView = view.findViewById(R.id.imgUser)
        val userName: TextView = view.findViewById(R.id.txtName)
        val phoneNumber: TextView = view.findViewById(R.id.txtPN)
        val email: TextView = view.findViewById(R.id.txtEMmail)
        val btnUnFollow: ImageButton = view.findViewById(R.id.btnUnFollow)
    }

    override fun getCount(): Int = list.size

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: ViewHolder
        val rowView: View

        if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            rowView = inflater.inflate(R.layout.custom_follow, parent, false)
            holder = ViewHolder(rowView)
            rowView.tag = holder
        } else {
            rowView = convertView
            holder = rowView.tag as ViewHolder
        }

        val currentItem = list[position]

        Glide.with(activity)
            .load(currentItem.avatarUrl.takeIf { !it.isNullOrEmpty() } ?: R.drawable.icon_avatar)
            .circleCrop()
            .into(holder.avatar)

        holder.userName.text = currentItem.userName
        holder.phoneNumber.text = currentItem.phoneNumber
        holder.email.text = currentItem.email

        // Click toàn item
        rowView.setOnClickListener {
            itemClickListener?.onItemClick(position)
        }

        // Click vào nút unFollow
        holder.btnUnFollow.setOnClickListener {
            followClickListener?.onFollowClick(position)
        }


        return rowView
    }

    // Định nghĩa interface cho item click
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    // Định nghĩa interface cho follow/unfollow click
    interface OnFollowClickListener {
        fun onFollowClick(position: Int)
    }


    // Cung cấp phương thức để set listener cho item click
    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    // Cung cấp phương thức để set listener cho follow click
    fun setOnFollowClickListener(listener: OnFollowClickListener) {
        followClickListener = listener
    }

}
