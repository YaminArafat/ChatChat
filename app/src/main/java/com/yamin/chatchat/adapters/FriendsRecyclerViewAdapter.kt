package com.yamin.chatchat.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import com.yamin.chatchat.databinding.FriendsRecyclerViewItemsBinding

class FriendsRecyclerViewAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    RecyclerView.Adapter<FriendsRecyclerViewAdapter.FriendsViewHolder>() {

    inner class FriendsViewHolder(binding: FriendsRecyclerViewItemsBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsViewHolder {
        val viewBinding = FriendsRecyclerViewItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FriendsViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: FriendsViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}