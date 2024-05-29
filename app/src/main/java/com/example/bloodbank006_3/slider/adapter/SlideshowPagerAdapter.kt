package com.example.bloodbank006_3.slider.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.bloodbank006_3.slider.SlideFragment1
import com.example.bloodbank006_3.slider.SlideFragment2
import com.example.bloodbank006_3.slider.SlideFragment3
import com.example.bloodbank006_3.slider.SlideFragment4


class SlideshowPagerAdapter(fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    companion object {
        private const val NUM_PAGES = 4 // Number of total pages/slides
    }

    override fun getItem(position: Int): Fragment {
        // Return the corresponding fragment based on the position
        return when (position) {
            0 -> SlideFragment1()
            1 -> SlideFragment2()
            2 -> SlideFragment3()
            3 -> SlideFragment4()
            // Add more cases for additional slide fragments
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }

    override fun getCount(): Int {
        return NUM_PAGES
    }
}

