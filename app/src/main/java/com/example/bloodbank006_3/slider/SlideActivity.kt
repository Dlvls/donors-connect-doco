package com.example.bloodbank006_3.slider

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.example.bloodbank006_3.R
import com.example.bloodbank006_3.databinding.ActivitySlideBinding
import com.example.bloodbank006_3.MainActivity
import com.example.bloodbank006_3.slider.adapter.SlideshowPagerAdapter
import com.google.android.material.snackbar.Snackbar

class SlideActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySlideBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySlideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val shouldShowSnackbar = sharedPreferences.getBoolean("showSnackbar", true)
        if (shouldShowSnackbar) {
            showSnackbar()
        }

        viewPager = findViewById(R.id.viewPager)
        val adapter = SlideshowPagerAdapter(supportFragmentManager)
        viewPager.adapter = adapter

        binding.ivHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun showSnackbar() {
        val snackbar = Snackbar.make(binding.root, "Slide to see more", Snackbar.LENGTH_INDEFINITE)
        val snackbarView = snackbar.view

        snackbarView.setBackgroundColor(Color.parseColor("#333333"))
        snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.white))

        val snackbarTextView =
            snackbarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
        snackbarTextView.setTextColor(Color.WHITE)

        snackbar.setAction("Dismiss") { snackbar.dismiss() }

        snackbar.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
                // Save the preference to not show the Snackbar again
                sharedPreferences.edit().putBoolean("showSnackbar", false).apply()
            }
        })

        // Dismiss Snackbar after 3 seconds
        val handler = Handler()
        val runnable = Runnable { snackbar.dismiss() }
        handler.postDelayed(runnable, 3000)

        snackbar.show()
    }


}