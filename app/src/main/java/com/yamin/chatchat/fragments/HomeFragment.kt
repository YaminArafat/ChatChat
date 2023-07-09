package com.yamin.chatchat.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.tabs.TabLayoutMediator
import com.yamin.chatchat.adapters.ViewPagerAdapter
import com.yamin.chatchat.databinding.FragmentHomeBinding

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {

    private var _viewBinding: FragmentHomeBinding? = null
    private val viewBinding get() = _viewBinding

    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView")
        userId = arguments?.getString("userId")
        _viewBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated")
        super.onViewCreated(view, savedInstanceState)

        viewBinding?.apply {
            val adapter = ViewPagerAdapter(childFragmentManager, lifecycle)
            viewPager.adapter = adapter

            val color = viewPager.background
            tabLayout.background = color
            TabLayoutMediator(tabLayout, viewPager, false, true) { tab, position ->
                val icon = adapter.getTabIcon(position)
                icon?.let {
                    tab.icon = AppCompatResources.getDrawable(requireContext(), it)
                }
                val title = adapter.getTabTitle(position)
                title?.let {
                    tab.text = getText(title)
                }
            }.attach()
        }

    }

    override fun onResume() {
        Log.d(TAG, "onResume")
        super.onResume()
    }

    override fun onPause() {
        Log.d(TAG, "onPause")
        super.onPause()
    }

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView")

        super.onDestroyView()
        _viewBinding = null
    }

    companion object {
        const val TAG = "HomeFragment"
    }
}