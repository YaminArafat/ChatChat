package com.yamin.chatchat.activites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yamin.chatchat.R
import com.yamin.chatchat.fragments.AppWelcomeScreenFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setWelcomeScreenFragment()
    }

    private fun setWelcomeScreenFragment() {
        supportFragmentManager.beginTransaction().add(R.id.main_activity_fragment_container, AppWelcomeScreenFragment()).commit()
    }
}