package com.yamin.chatchat.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yamin.chatchat.data.models.Chats
import com.yamin.chatchat.databinding.ChatsRecyclerViewItemsBinding
import com.yamin.chatchat.helpers.OnItemClickListener
import com.yamin.chatchat.utils.CommonUtils
import de.hdodenhof.circleimageview.CircleImageView

class ChatsTabRecyclerViewAdapter(
    private val chatsList: ArrayList<Chats>,
    private val currentUserId: String,
    private val startChat: OnItemClickListener
) :
    RecyclerView.Adapter<ChatsTabRecyclerViewAdapter.ChatsTabViewHolder>() {

    private lateinit var viewBinding: ChatsRecyclerViewItemsBinding

    inner class ChatsTabViewHolder(binding: ChatsRecyclerViewItemsBinding) : RecyclerView.ViewHolder(binding.root) {
        val profileImage: CircleImageView = binding.chatsProfilePic
        val profileName: TextView = binding.chatsProfileName
        val lastMessage: TextView = binding.chatsMessages
        val lastMessageTime: TextView = binding.chatsTime
        val seenMessage: CircleImageView = binding.chatsSeenProfilePic

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                startChat.onClick(chatsList[position].userId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsTabViewHolder {
        viewBinding = ChatsRecyclerViewItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ChatsTabViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: ChatsTabViewHolder, position: Int) {

        holder.apply {
            Glide.with(itemView).load(chatsList[position].userProfileImage).into(profileImage)
            val displayName = chatsList[position].userFirstName + " " + chatsList[position].userLastName
            profileName.text = displayName
            val lastMessageData = chatsList[position].lastMessage
            lastMessageTime.text = CommonUtils.getFormattedTime(lastMessageData.messageTimestamp)
            if (lastMessageData.senderId == currentUserId) {
                val lastMessageDisplayContent = "You: " + lastMessageData.messageText
                lastMessage.text = lastMessageDisplayContent
                Glide.with(itemView).load(chatsList[position].userProfileImage).into(seenMessage)
                seenMessage.visibility = View.VISIBLE
            } else {
                lastMessage.text = lastMessageData.messageText
                seenMessage.visibility = View.INVISIBLE
            }
        }
    }

    override fun getItemCount(): Int {
        return chatsList.size
    }

    fun updateData(newData: ArrayList<Chats>) {
        Log.d(TAG, "updateData")
        chatsList.clear()
        chatsList.addAll(newData)
        notifyDataSetChanged()
    }

    companion object {
        const val TAG = "FriendsViewModel"
    }
}