package com.app.jonathanchiou.willimissbart.bottomnav

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.app.jonathanchiou.willimissbart.R
import java.util.*

interface FragmentFactory {
    fun create(index: Int): Fragment
}

class BottomNavigationView(context: Context,
                           attributeSet: AttributeSet?):
    LinearLayout(context, attributeSet) {

    private var isBeingClicked = false

    private lateinit var fragmentManager: FragmentManager
    private lateinit var fragmentFactory: FragmentFactory
    private val insertionStack = LinkedList<String>()

    private var containerId = 0

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

        val fragment = fragmentManager.findFragmentByTag(indexTag) ?: let {
            val freshFragment = fragmentFactory.create(index)

            fragmentManager
                .beginTransaction()
                .add(containerId, freshFragment, indexTag)
                .commit()

            freshFragment
        }

        if (insertionStack.contains(indexTag)) {
            while (insertionStack.last != indexTag) {
                insertionStack.removeLast()
                fragmentManager.popBackStack()
            }
        } else {
            fragmentManager
                .beginTransaction()
                .let {
                    if (!insertionStack.isEmpty()) {
                        it.hide(fragmentManager.findFragmentByTag(insertionStack.last)!!)
                    }

                    it
                }
                .show(fragment)
                .addToBackStack(null)
                .commit()

            insertionStack.add(indexTag)
        }
    }

    fun onBackPressed(): Boolean {
        return if (fragmentManager.backStackEntryCount <= 1) {
            false
        } else {
            fragmentManager.popBackStack()
            insertionStack.pop()
            true
        }
    }
}