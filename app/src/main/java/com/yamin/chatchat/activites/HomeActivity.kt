package com.yamin.chatchat.activites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yamin.chatchat.R
import com.yamin.chatchat.fragments.HomeFragment

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val bundle = Bundle()
        val userId = intent.getStringExtra("userId")
        bundle.putString("userId", userId)
        setUpHomeFragment(bundle)
    }

    private fun setUpHomeFragment(bundle: Bundle) {
        val homeFragment = HomeFragment()
        homeFragment.arguments = bundle
        supportFragmentManager.beginTransaction().replace(R.id.home_activity_fragment_container, homeFragment).commit()
    }
}