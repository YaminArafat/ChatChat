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
import com.yamin.chatchat.databinding.MyRequestsRecyclerViewItemsBinding
import com.yamin.chatchat.helpers.OnItemClickListener
import de.hdodenhof.circleimageview.CircleImageView

class MyRequestsRecyclerViewAdapter(private val requestsList: ArrayList<Friend>, private val acceptRequestListener: OnItemClickListener) :
    RecyclerView.Adapter<MyRequestsRecyclerViewAdapter.MyRequestsViewHolder>() {

    inner class MyRequestsViewHolder(binding: MyRequestsRecyclerViewItemsBinding) : RecyclerView.ViewHolder(binding.root) {
        val profileImage: CircleImageView = binding.friendsProfilePic
        val profileName: TextView = binding.friendsProfileName
        val email: TextView = binding.friendsEmail
        val actionButton: FloatingActionButton = binding.actionButton

        init {
            binding.root.setOnClickListener {

            }
            actionButton.setOnClickListener {
                val position = adapterPosition
                acceptRequestListener.onClick(requestsList[position].id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyRequestsViewHolder {
        val viewBinding = MyRequestsRecyclerViewItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyRequestsViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: MyRequestsViewHolder, position: Int) {
        holder.apply {
            Glide.with(itemView).load(requestsList[position].profileImage).into(profileImage)
            val displayName = requestsList[position].firstName + " " + requestsList[position].lastName
            profileName.text = displayName
            email.text = requestsList[position].email
            actionButton.setImageResource(R.drawable.ic_baseline_check_circle_outline_24)
        }
    }

    override fun getItemCount(): Int {
        return requestsList.size
    }

    fun updateData(newData: ArrayList<Friend>) {
        Log.d(TAG, "updateData")
        requestsList.clear()
        requestsList.addAll(newData)
        notifyDataSetChanged()
    }

    companion object {
        const val TAG = "MyRequestsRecyclerViewAdapter"
    }
}