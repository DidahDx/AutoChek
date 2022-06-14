package com.github.didahdx.autochek.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.github.didahdx.autochek.R
import com.github.didahdx.autochek.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bottomNavigation.background = null
//        binding.bottomNavigation.menu.getItem(2).isEnabled = false

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)
        appBarConfiguration = AppBarConfiguration.Builder(
            R.id.homeFragment,
            R.id.chatFragment, R.id.notificationFragment, R.id.favouriteFragment
        ).build()
//        setupActionBarWithNavController(navController, appBarConfiguration)

    }
}