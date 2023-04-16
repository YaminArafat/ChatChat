package com.yamin.chatchat.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yamin.chatchat.R
import com.yamin.chatchat.databinding.ChatsRecyclerViewItemsBinding
import de.hdodenhof.circleimageview.CircleImageView

class ChatsRecyclerViewAdapter : RecyclerView.Adapter<ChatsRecyclerViewAdapter.ChatsViewHolder>() {

    private lateinit var viewBinding: ChatsRecyclerViewItemsBinding

    inner class ChatsViewHolder(binding: ChatsRecyclerViewItemsBinding) : RecyclerView.ViewHolder(binding.root) {
        val profileImage: CircleImageView = binding.chatsProfilePic
        val profileName: TextView = binding.chatsProfileName
        val lastMessage: TextView = binding.chatsMessages
        val lastMessageTime: TextView = binding.chatsTime
        val seenMessage: CircleImageView = binding.chatsSeenProfilePic

        init {
            binding.root.setOnClickListener {

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsViewHolder {
        viewBinding = ChatsRecyclerViewItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ChatsViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: ChatsViewHolder, position: Int) {

        holder.profileImage.setImageResource(R.drawable.ic_baseline_people_outline_24)
        holder.profileName.text = "Yamin Arafat"
        holder.lastMessage.text = "last message"
        holder.lastMessageTime.text = "12:34 pm"
        holder.seenMessage.setImageResource(R.drawable.ic_baseline_people_outline_24)
    }

    override fun getItemCount(): Int {
        return 1
    }
}