package com.example.bloodbank006_3.profile.pager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class PagerAdapter(fragmentActivity: FragmentActivity, private val tabTitles: Array<String>) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return tabTitles.size
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> DonationFragment()
            1 -> RequestFragment()
            else -> throw IllegalArgumentException("Invalid tab position: $position")
        }
    }
}