package com.yamin.chatchat.activites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.yamin.chatchat.R
import com.yamin.chatchat.fragments.AppWelcomeScreenFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setWelcomeScreenFragment()
    }

    private fun setWelcomeScreenFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.main_activity_fragment_container, AppWelcomeScreenFragment()).commit()
    }

    override fun onPause() {
        Log.d(TAG, "onPause")
        super.onPause()
    }

    override fun onResume() {
        Log.d(TAG, "onResume")
        super.onResume()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        super.onDestroy()
    }

    companion object {
        const val TAG = "MainActivity"
    }
}