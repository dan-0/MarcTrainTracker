/*
 * Copyright (c) 2019 IdleOffice Inc.
 *
 * MainActivity.kt is part of MarcTrainTracker.
 *
 * MarcTrainTracker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MarcTrainTracker is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.idleoffice.marctrain.ui.main

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.idleoffice.marctrain.R
import com.idleoffice.marctrain.databinding.ActivityMainBinding
import com.idleoffice.marctrain.extensions.findNavController
import com.idleoffice.marctrain.vibrateTap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private var backButtonCount = 0

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        backButtonCount = 0

        setupNavigation()
    }

    private fun setupNavigation() {
        val navView: BottomNavigationView = binding.navigation

        val navController = findNavController()

        navView.setupWithNavController(navController)
    }

    override fun onBackPressed() {

        val currentDestination = findNavController().currentDestination?.id

        when {
            backButtonCount == 0 && currentDestination == R.id.navigation_status -> delayBackPress()
            else -> super.onBackPressed()
        }
    }

    private fun delayBackPress() {

        backButtonCount++

        vibrateTap()
        Toast.makeText(this, getString(R.string.back_to_exit), Toast.LENGTH_SHORT).show()
        lifecycleScope.launchWhenCreated {
            withContext(Dispatchers.IO) {
                delay(3000)
            }
            backButtonCount = 0
        }
    }
}
