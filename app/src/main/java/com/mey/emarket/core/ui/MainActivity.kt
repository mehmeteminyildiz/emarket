package com.mey.emarket.core.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.mey.emarket.R
import com.mey.emarket.databinding.ActivityMainBinding
import com.mey.emarket.features.cart.viewmodel.CartViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private lateinit var cartViewModel: CartViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cartViewModel = ViewModelProvider(this)[CartViewModel::class.java]

        val navGraph =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navGraph.navController
        binding.bottomNavigationView.setupWithNavController(navController)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->

            val destination = item.itemId
            val currentDestination = navController.currentDestination?.id
            if (destination == currentDestination) {
                return@setOnNavigationItemSelectedListener false
            }
            NavigationUI.onNavDestinationSelected(item, navController)
        }

        observeCartItemCount()
    }

    private fun observeCartItemCount() {
        val badge = binding.bottomNavigationView.getOrCreateBadge(R.id.cartFragment)
        badge.backgroundColor = ContextCompat.getColor(this, R.color.red) // Arka plan rengi
        badge.badgeTextColor = ContextCompat.getColor(this, R.color.white) // Metin rengi

        cartViewModel.cartItemCount.observe(this) {
            if (it > 0) {
                badge.isVisible = true
                badge.number = it
            } else {
                badge.isVisible = false
            }
        }

    }
}