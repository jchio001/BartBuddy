package com.app.jonathanchiou.willimissbart.navigation.bottomnav

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.app.jonathanchiou.willimissbart.R
import java.util.*
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

interface FragmentFactory {
    fun create(index: Int): Fragment
}

class BottomNavigationView(context: Context,
                           attributeSet: AttributeSet):
    LinearLayout(context, attributeSet) {

    private var isBeingClicked = false

    private lateinit var fragmentManager: FragmentManager
    private lateinit var fragmentFactory: FragmentFactory
    private val insertionStack = LinkedList<String>()

    private var containerId = 0

    private val baseTextSize: Int
    private val focusedTextSize: Int

    private val baseIconSize: Int
    private val focusedIconSize: Int

    init {
        View.inflate(context, R.layout.bottom_navigation_view, this)

        val resources = resources
        val attributes = resources.obtainAttributes(attributeSet, R.styleable.BottomNavigationView)

        try {
            baseTextSize = attributes.getDimensionPixelSize(
                R.styleable.BottomNavigationView_android_textSize,
                resources.getDimensionPixelSize(R.dimen.default_text_size))
            focusedTextSize = (baseTextSize * 1.2).toInt()

            baseIconSize = baseTextSize * 2
            focusedIconSize = (baseIconSize * 1.2).toInt()
        } finally {
            attributes.recycle()
        }

        val debouncedOnClickListener = OnClickListener {
            if (!isBeingClicked) {
                isBeingClicked = true
                setSelection(indexOfChild(it))
                isBeingClicked = false
            }
        }

        for (i in 0 until childCount) {
            (getChildAt(i) as ViewGroup).also { child ->
                child.setOnClickListener(debouncedOnClickListener)
                sizeMenuItem(child, baseIconSize, baseTextSize)
            }
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
            var hasPopped = false
            while (insertionStack.last != indexTag) {

                if (!hasPopped) {
                    sizeMenuItem(
                        insertionStack.removeLast(),
                        baseIconSize,
                        baseTextSize)
                    hasPopped = true
                }

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

            if (!insertionStack.isEmpty()) {
                sizeMenuItem(
                    insertionStack.last,
                    baseIconSize,
                    baseTextSize)
            }

            insertionStack.add(indexTag)
        }

        sizeMenuItem(
            indexTag,
            focusedIconSize,
            focusedTextSize)
    }

    fun onBackPressed(): Boolean {
        return if (fragmentManager.backStackEntryCount <= 1) {
            false
        } else {
            fragmentManager.popBackStack()

            sizeMenuItem(
                insertionStack.removeLast(),
                baseIconSize,
                baseTextSize)

            sizeMenuItem(
                insertionStack.last(),
                focusedIconSize,
                focusedTextSize)

            true
        }
    }

    private fun sizeMenuItem(tag: String, iconSize: Int, textSize: Int) {
        sizeMenuItem(getChildAt(Integer.valueOf(tag)) as ViewGroup, iconSize, textSize)
    }
}

private fun sizeMenuItem(menuItem: ViewGroup, iconSize: Int, textSize: Int) {
    val menuItemChild = menuItem.getChildAt(0) as ViewGroup

    (menuItemChild.getChildAt(0) as ImageView).also {
        val layoutParams = it.layoutParams
        layoutParams.width = iconSize
        layoutParams.height = iconSize
        it.layoutParams = layoutParams
    }

    (menuItemChild.getChildAt(1) as TextView)
        .setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
}