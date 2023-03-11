package com.yamin.chatchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yamin.chatchat.fragments.AppWelcomeScreenFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setWelcomeScreenFragment()
    }

    private fun setWelcomeScreenFragment(){
        val welcomeScreenFragment = AppWelcomeScreenFragment()
        supportFragmentManager.beginTransaction().add(R.id.main_activity_fragment_container, welcomeScreenFragment).commit()
    }
}