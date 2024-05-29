package com.example.bloodbank006_3

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.bloodbank006_3.databinding.ActivityMainBinding
import com.example.bloodbank006_3.form.FormFragment
import com.example.bloodbank006_3.home.HomeFragment
import com.example.bloodbank006_3.profile.ProfileFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val homeFragment = HomeFragment()
    private val formFragment = FormFragment()
    private val profileFragment = ProfileFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_item_1 -> {
                    switchFragment(homeFragment)
                    true
                }
                R.id.navigation_item_2 -> {
                    switchFragment(formFragment)
                    true
                }
                R.id.navigation_item_3 -> {
                    switchFragment(profileFragment)
                    true
                }
                else -> false
            }
        }

        // Set the initial fragment
        switchFragment(homeFragment)
    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }
}
