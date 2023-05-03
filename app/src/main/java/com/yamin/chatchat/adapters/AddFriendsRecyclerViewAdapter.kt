package com.yamin.chatchat.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.yamin.chatchat.R
import com.yamin.chatchat.data.models.Friend
import com.yamin.chatchat.databinding.AddFriendsRecyclerViewItemsBinding
import com.yamin.chatchat.helpers.OnItemClickListener
import de.hdodenhof.circleimageview.CircleImageView

class AddFriendsRecyclerViewAdapter(private val userList: ArrayList<Friend>, private val sendRequestListener: OnItemClickListener) :
    RecyclerView.Adapter<AddFriendsRecyclerViewAdapter.AddFriendsViewHolder>() {

    inner class AddFriendsViewHolder(binding: AddFriendsRecyclerViewItemsBinding) : RecyclerView.ViewHolder(binding.root) {
        val profileImage: CircleImageView = binding.friendsProfilePic
        val profileName: TextView = binding.friendsProfileName
        val email: TextView = binding.friendsEmail
        val actionButton: FloatingActionButton = binding.actionButton

        init {
            binding.root.setOnClickListener {

            }
            actionButton.setOnClickListener {
                val position = adapterPosition
                sendRequestListener.onClick(userList[position].id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddFriendsViewHolder {
        val viewBinding = AddFriendsRecyclerViewItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddFriendsViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: AddFriendsViewHolder, position: Int) {
        holder.apply {
            Glide.with(itemView).load(userList[position].profileImage).into(profileImage)
            val displayName = userList[position].firstName + " " + userList[position].lastName
            profileName.text = displayName
            email.text = userList[position].email
            actionButton.setImageResource(R.drawable.ic_baseline_person_add_alt_1_24)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun updateData(newData: ArrayList<Friend>) {
        Log.d(TAG, "updateData")
        userList.clear()
        userList.addAll(newData)
        notifyDataSetChanged()
    }

    companion object {
        const val TAG = "AddFriendsRecyclerViewAdapter"
    }
}