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
import com.yamin.chatchat.databinding.SentRequestsRecyclerViewItemsBinding
import com.yamin.chatchat.helpers.OnItemClickListener
import de.hdodenhof.circleimageview.CircleImageView

class SentRequestsRecyclerViewAdapter(private val sentRequestsList: ArrayList<Friend>, private val requestCancelListener: OnItemClickListener) :
    RecyclerView.Adapter<SentRequestsRecyclerViewAdapter.SentRequestViewHolder>() {
    inner class SentRequestViewHolder(binding: SentRequestsRecyclerViewItemsBinding) : RecyclerView.ViewHolder(binding.root) {
        val profileImage: CircleImageView = binding.friendsProfilePic
        val profileName: TextView = binding.friendsProfileName
        val email: TextView = binding.friendsEmail
        val actionButton: FloatingActionButton = binding.actionButton

        init {
            binding.root.setOnClickListener {

            }
            actionButton.setOnClickListener {
                val position = adapterPosition
                requestCancelListener.onClick(sentRequestsList[position].id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SentRequestViewHolder {
        val viewBinding = SentRequestsRecyclerViewItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SentRequestViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: SentRequestViewHolder, position: Int) {
        holder.apply {
            Glide.with(itemView).load(sentRequestsList[position].profileImage).into(profileImage)
            val displayName = sentRequestsList[position].firstName + " " + sentRequestsList[position].lastName
            profileName.text = displayName
            email.text = sentRequestsList[position].email
            actionButton.setImageResource(R.drawable.ic_baseline_cancel_24)
        }
    }

    override fun getItemCount(): Int {
        return sentRequestsList.size
    }

    fun updateData(newData: ArrayList<Friend>) {
        Log.d(TAG, "updateData")
        sentRequestsList.clear()
        sentRequestsList.addAll(newData)
        notifyDataSetChanged()
    }

    companion object {
        const val TAG = "SentRequestsRecyclerViewAdapter"
    }
}