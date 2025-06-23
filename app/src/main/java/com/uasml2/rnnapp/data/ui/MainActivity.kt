package com.uasml2.rnnapp.data.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.uasml2.rnnapp.R
import com.uasml2.rnnapp.ViewPagerAdapter

class MainActivity : AppCompatActivity() {

    private val tabTitles = listOf("Tentang", "Dataset", "Fitur", "Arsitektur", "Simulasi")
    private val fragments = listOf(
        AboutFragment(),
        DatasetFragment(),
        FeaturesFragment(),
        ArchitectureFragment(),
        SimulationFragment()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val navView = findViewById<NavigationView>(R.id.nav_view)
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_info -> {
                    android.app.AlertDialog.Builder(this)
                        .setTitle("Info Pengembang")
                        .setMessage("Dikembangkan oleh:\nNama: Muhamad Hernan Fauzan\nKelas: Malam A\nMata Kuliah: Machine Learning 2\nNIM: 241352002")
                        .setPositiveButton("Tutup", null)
                        .show()
                }

                R.id.nav_rate -> {
                    Toast.makeText(this, "Fitur rating coming soon!", Toast.LENGTH_SHORT).show()
                }

            }
            drawerLayout.closeDrawers()
            true
        }

        viewPager.adapter = ViewPagerAdapter(this, fragments)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }
}