package com.moskofidi.fintech.adapters

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.*
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.moskofidi.fintech.fragments.FragmentHot
import com.moskofidi.fintech.fragments.FragmentLatest
import com.moskofidi.fintech.fragments.FragmentTop

class TabAdapter (activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                FragmentLatest()
            }
            1 -> FragmentTop()
            else -> {
                return FragmentHot()
            }
        }
    }
}