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
import com.yamin.chatchat.databinding.MyFriendsRecyclerViewItemsBinding
import com.yamin.chatchat.helpers.OnItemClickListener
import de.hdodenhof.circleimageview.CircleImageView

class MyFriendsRecyclerViewAdapter(private val myFriendsList: ArrayList<Friend>, private val startChatListener: OnItemClickListener) :
    RecyclerView.Adapter<MyFriendsRecyclerViewAdapter.MyFriendsViewHolder>() {
    inner class MyFriendsViewHolder(binding: MyFriendsRecyclerViewItemsBinding) : RecyclerView.ViewHolder(binding.root) {
        val profileImage: CircleImageView = binding.friendsProfilePic
        val profileName: TextView = binding.friendsProfileName
        val email: TextView = binding.friendsEmail
        val actionButton: FloatingActionButton = binding.actionButton

        init {
            binding.root.setOnClickListener {

            }
            actionButton.setOnClickListener {
                val position = adapterPosition
                startChatListener.onClick(myFriendsList[position].id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyFriendsViewHolder {
        val viewBinding = MyFriendsRecyclerViewItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyFriendsViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: MyFriendsViewHolder, position: Int) {
        holder.apply {
            Glide.with(itemView).load(myFriendsList[position].profileImage).into(profileImage)
            val displayName = myFriendsList[position].firstName + " " + myFriendsList[position].lastName
            profileName.text = displayName
            email.text = myFriendsList[position].email
            actionButton.setImageResource(R.drawable.ic_baseline_message_24)
        }
    }

    override fun getItemCount(): Int {
        return myFriendsList.size
    }

    fun updateData(newData: ArrayList<Friend>) {
        Log.d(TAG, "updateData")
        myFriendsList.clear()
        myFriendsList.addAll(newData)
        notifyDataSetChanged()
    }

    companion object {
        const val TAG = "MyFriendsRecyclerViewAdapter"
    }
}