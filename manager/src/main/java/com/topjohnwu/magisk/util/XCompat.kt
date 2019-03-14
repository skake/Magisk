package com.topjohnwu.magisk.util

import androidx.core.view.children
import androidx.navigation.NavController
import com.google.android.material.bottomnavigation.BottomNavigationView

fun BottomNavigationView.setupWith(navController: NavController) {
    setOnNavigationItemSelectedListener {
        navController.navigate(it.itemId)
        true
    }

    //Prefix for reselection. Since listener is implemented in a way that nobody can comprehend - by default when no
    //reselected listener is set, it automatically redirects the event to onSelectedListener. Such a dumb design...
    setOnNavigationItemReselectedListener {}

    navController.addOnDestinationChangedListener { _, destination, _ ->
        menu.children.forEach {
            if (destination.id == it.itemId) {
                it.isChecked = true
            }
        }
    }
}