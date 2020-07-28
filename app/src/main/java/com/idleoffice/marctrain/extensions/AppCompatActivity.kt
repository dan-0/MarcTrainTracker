package com.idleoffice.marctrain.extensions

import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.idleoffice.marctrain.R

/**
 * Finds a nav controller given [hostFragmentId]
 */
fun AppCompatActivity.findNavController(
    hostFragmentId: Int = R.id.nav_host_fragment
): NavController {
    val navHostFragment = supportFragmentManager.findFragmentById(hostFragmentId) as NavHostFragment
    return navHostFragment.navController
}