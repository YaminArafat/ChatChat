package com.yamin.chatchat.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.yamin.chatchat.R
import com.yamin.chatchat.fragments.ActiveFriendsTabFragment
import com.yamin.chatchat.fragments.ChatsTabFragment
import com.yamin.chatchat.fragments.FriendsTabFragment

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ChatsTabFragment()
            1 -> ActiveFriendsTabFragment()
            2 -> FriendsTabFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }

    fun getTabTitle(position: Int): Int? {
        return when (position) {
            0 -> R.string.chats
            1 -> R.string.active_friends
            2 -> R.string.friends
            else -> null
        }
    }

    fun getTabIcon(position: Int): Int? {
        return when (position) {
            0 -> R.drawable.ic_baseline_chat_bubble_outline_24
            1 -> R.drawable.ic_baseline_people_outline_24
            2 -> R.drawable.ic_baseline_person_add_alt_24
            else -> null
        }
    }
}