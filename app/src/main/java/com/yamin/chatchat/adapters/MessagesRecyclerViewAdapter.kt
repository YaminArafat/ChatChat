package com.yamin.chatchat.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yamin.chatchat.data.models.Message
import com.yamin.chatchat.databinding.MessagesRecyclerViewItemsBinding
import com.yamin.chatchat.utils.CommonUtils
import de.hdodenhof.circleimageview.CircleImageView
import kotlin.collections.ArrayList

class MessagesRecyclerViewAdapter(
    private val fullConversation: ArrayList<Message>,
    private val receiverId: String
) :
    RecyclerView.Adapter<MessagesRecyclerViewAdapter.MessagesViewHolder>() {
    private val itemClickTracer: MutableMap<Int, Boolean> = mutableMapOf()

    inner class MessagesViewHolder(binding: MessagesRecyclerViewItemsBinding) : RecyclerView.ViewHolder(binding.root) {
        val messageView: TextView = binding.messageText
        val messageTime: TextView = binding.messageTime
        val messageLayout: LinearLayout = binding.messageLayout
        val receiverProfilePic: CircleImageView = binding.profilePic

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesViewHolder {
        Log.d(TAG, "onCreateViewHolder")
        val viewBinding = MessagesRecyclerViewItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessagesViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: MessagesViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder")
        holder.apply {
            messageView.text = fullConversation[position].messageText
            val itemClicked = itemClickTracer[position] ?: false
            itemView.setOnClickListener {
                when (itemClicked) {
                    true -> {
                        messageTime.visibility = View.GONE
                    }
                    false -> {
                        messageTime.text = CommonUtils.getFormattedTime(fullConversation[position].messageTimestamp)
                        messageTime.visibility = View.VISIBLE
                    }
                }
                itemClickTracer[position] = !itemClicked
            }
            val layoutParamsForMargin = messageLayout.layoutParams as ViewGroup.MarginLayoutParams
            val layoutParamsForAlign = messageLayout.layoutParams as RelativeLayout.LayoutParams
            if (fullConversation[position].receiverId == receiverId) {
                receiverProfilePic.visibility = View.GONE
                layoutParamsForAlign.addRule(RelativeLayout.ALIGN_PARENT_END)
                layoutParamsForMargin.setMargins(30, 0, 0, 0)
                messageLayout.layoutParams = layoutParamsForAlign
                messageLayout.layoutParams = layoutParamsForMargin
            } else {
                receiverProfilePic.visibility = View.VISIBLE ///TODO
                layoutParamsForMargin.setMargins(0, 0, 30, 0)
                messageLayout.layoutParams = layoutParamsForMargin
            }
        }
    }

    override fun getItemCount(): Int {
        return fullConversation.size
    }

    fun updateConversation(newData: Message) {
        Log.d(TAG, "updateConversation")
        fullConversation.add(newData)
        notifyItemInserted(fullConversation.size - 1)
    }

    companion object {
        const val TAG = "MessagesRecyclerViewAdapter"
    }
}