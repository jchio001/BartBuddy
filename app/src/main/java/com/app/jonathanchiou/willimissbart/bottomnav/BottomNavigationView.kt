package com.app.jonathanchiou.willimissbart.bottomnav

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.View.OnClickListener
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.app.jonathanchiou.willimissbart.R

interface FragmentFactory {
    fun create(index: Int): Fragment
}

class BottomNavigationView(context: Context,
                           attributeSet: AttributeSet?):
    LinearLayout(context, attributeSet) {

    private var isBeingClicked = false

    private lateinit var fragmentManager: FragmentManager
    private lateinit var fragmentFactory: FragmentFactory

    var containerId = 0

    init {
        View.inflate(context, R.layout.bottom_navigation_view, this)

        val debouncedOnClickListener = OnClickListener {
            if (!isBeingClicked) {
                isBeingClicked = true
                setSelection(indexOfChild(it))
                isBeingClicked = false
            }
        }

        for (i in 0 until childCount) {
            getChildAt(i).setOnClickListener(debouncedOnClickListener)
        }
    }

    fun setFragmentManager(fragmentManager: FragmentManager,
                           containerId: Int,
                           fragmentFactory: FragmentFactory) {
        this.fragmentManager = fragmentManager
        this.containerId = containerId
        this.fragmentFactory = fragmentFactory
    }

    fun setSelection(index: Int) {
        val indexTag = index.toString()

        val fragment = fragmentManager.findFragmentByTag(indexTag)
            ?: fragmentFactory.create(index)

        if (!fragment.isVisible) {
            fragmentManager
                .beginTransaction()
                .replace(containerId, fragment, indexTag)
                .addToBackStack(null)
                .commit()
        }
    }
}