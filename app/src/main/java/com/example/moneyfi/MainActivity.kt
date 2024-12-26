package com.example.moneyfi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.moneyfi.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        // Get the NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        // Get the NavController from the NavHostFragment
        val navController = navHostFragment.navController

        // Setup the bottom navigation with the NavController
        binding.bottomNavigation.setupWithNavController(navController)

        binding.fabAdd.setOnClickListener {
            // TODO: Handle FAB click
        }
    }
}