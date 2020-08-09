package com.app.jonathan.willimissbart.navigation.bottomnav

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.app.jonathan.willimissbart.R
import com.app.jonathan.willimissbart.utils.view.DebouncingOnClickListener

interface FragmentFactory {
    fun create(index: Int): Fragment
}

@OptIn(kotlin.ExperimentalStdlibApi::class)
class BottomNavigationView(
    context: Context,
    attributeSet: AttributeSet
) : LinearLayout(context, attributeSet) {

    private lateinit var fragmentManager: FragmentManager
    private lateinit var fragmentFactory: FragmentFactory

    private var insertionStack = ArrayList<String>(2)

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

            baseIconSize = (baseTextSize * 1.8).toInt()
            focusedIconSize = (baseIconSize * 1.2).toInt()
        } finally {
            attributes.recycle()
        }

        val debouncedOnClickListener = DebouncingOnClickListener { view ->
            setSelection(indexOfChild(view))
        }

        for (i in 0 until childCount) {
            (getChildAt(i) as ViewGroup).also { child ->
                child.setOnClickListener(debouncedOnClickListener)
                child.sizeMenuItem(baseIconSize, baseTextSize)
            }
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        return Bundle().apply {
            putParcelable(SUPER_STATE, super.onSaveInstanceState())
            putStringArrayList(INSERTION_STACK, insertionStack)
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        val savedStateBundle = state as? Bundle?

        if (savedStateBundle != null) {
            super.onRestoreInstanceState(savedStateBundle.getParcelable<View.BaseSavedState>(SUPER_STATE))
        }

        insertionStack = savedStateBundle?.getStringArrayList(INSERTION_STACK) ?: insertionStack

        insertionStack.lastOrNull()?.let { lastTag ->
            sizeMenuItem(
                lastTag,
                focusedIconSize,
                focusedTextSize
            )
        }
    }

    fun setFragmentManager(
        fragmentManager: FragmentManager,
        containerId: Int,
        fragmentFactory: FragmentFactory
    ) {
        this.fragmentManager = fragmentManager
        this.containerId = containerId
        this.fragmentFactory = fragmentFactory

        if (fragmentManager.fragments.isEmpty()) {
            setSelection(0)
        }
    }

    private fun setSelection(index: Int) {
        val indexTag = index.toString()

        val fragment = fragmentManager.findFragmentByTag(indexTag) ?: let {
            val freshFragment = fragmentFactory.create(index)

            fragmentManager
                .beginTransaction()
                .add(containerId, freshFragment, indexTag)
                .commitNow()

            freshFragment
        }

        if (insertionStack.contains(indexTag)) {
            var hasPopped = false
            while (insertionStack.last() != indexTag) {

                if (!hasPopped) {
                    sizeMenuItem(
                        insertionStack.removeLast(),
                        baseIconSize,
                        baseTextSize
                    )
                    hasPopped = true
                }

                fragmentManager.popBackStack()
            }
        } else {
            fragmentManager
                .beginTransaction()
                .let { fragmentTransaction ->
                    if (insertionStack.isNotEmpty()) {
                        fragmentManager.findFragmentByTag(insertionStack.last())?.let(fragmentTransaction::hide)
                    }

                    fragmentTransaction
                }
                .show(fragment)
                .addToBackStack(null)
                .commit()

            if (insertionStack.isNotEmpty()) {
                sizeMenuItem(
                    insertionStack.last(),
                    baseIconSize,
                    baseTextSize
                )
            }

            insertionStack.add(indexTag)
        }

        sizeMenuItem(
            indexTag,
            focusedIconSize,
            focusedTextSize
        )
    }

    fun onBackPressed(): Boolean {
        return if (fragmentManager.backStackEntryCount <= 1) {
            false
        } else {
            fragmentManager.popBackStack()

            sizeMenuItem(
                insertionStack.removeLast(),
                baseIconSize,
                baseTextSize
            )

            sizeMenuItem(
                insertionStack.last(),
                focusedIconSize,
                focusedTextSize
            )

            true
        }
    }

    fun onHiddenChanged(hidden: Boolean) {
        if (hidden) {
            return
        }

        insertionStack
            .lastOrNull()
            ?.let(fragmentManager::findFragmentByTag)
            ?.onHiddenChanged(false)
    }

    private fun sizeMenuItem(tag: String, iconSize: Int, textSize: Int) {
        (getChildAt(Integer.valueOf(tag)) as ViewGroup).sizeMenuItem(iconSize, textSize)
    }

    private fun ViewGroup.sizeMenuItem(iconSize: Int, textSize: Int) {
        val menuItemChild = getChildAt(0) as ViewGroup

        (menuItemChild.getChildAt(0) as ImageView).also {
            val layoutParams = it.layoutParams
            layoutParams.width = iconSize
            layoutParams.height = iconSize
            it.layoutParams = layoutParams
        }

        (menuItemChild.getChildAt(1) as TextView)
            .setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
    }

    companion object {

        private const val SUPER_STATE = "super_state"
        private const val INSERTION_STACK = "insertion_stack"
    }
}
