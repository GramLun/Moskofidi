package com.moskofidi.fintech

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayoutMediator
import com.moskofidi.fintech.adapters.TabAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var fragmentAdapter: TabAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        DevApplication.setConnectionListener(this)
        DevApplication.setNetworkState()

        viewPager.adapter = TabAdapter(this)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text =
                when (position) {
                    0 -> "Последние"
                    1 -> "Лучшие"
                    else -> {
                        "Горячие"
                    }
                }
        }.attach()
    }

    override fun onResume() {
        super.onResume()
        DevApplication.regListener()
    }

    override fun onStop() {
        super.onStop()
        DevApplication.unregisterListener()
    }
}