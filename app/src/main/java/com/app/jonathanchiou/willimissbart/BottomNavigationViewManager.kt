package com.app.jonathanchiou.willimissbart

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class BottomNavigationViewManager(
    private val bottomNavigationView: BottomNavigationView,
    private val fragmentManager: FragmentManager,
    containerId: Int,
    fragmentFactory: FragmentFactory) {

    interface FragmentFactory {
        fun createFragment(itemId: Int): Fragment
    }

    init {
        val selectedId = bottomNavigationView.selectedItemId

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            val fragment = fragmentManager.findFragmentByTag(menuItem.title.toString())
                ?: fragmentFactory.createFragment(menuItem.itemId)

            if (!fragment.isVisible) {
                fragmentManager
                    .beginTransaction()
                    .replace(containerId, fragment, menuItem.title.toString())
                    .addToBackStack(null)
                    .commit()
            }

            true
        }
    }

    fun setSelectedItem(itemId: Int) {
        bottomNavigationView.selectedItemId = itemId
    }

    fun onBackPressed(): Boolean {
        return if (fragmentManager.backStackEntryCount <= 1) {
            false
        } else {
            fragmentManager.popBackStack()
            true
        }
    }
}