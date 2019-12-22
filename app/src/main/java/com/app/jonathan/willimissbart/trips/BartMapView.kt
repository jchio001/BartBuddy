package com.app.jonathan.willimissbart.trips

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.app.jonathan.willimissbart.R

class BartMapView(
    context: Context,
    attributeSet: AttributeSet
) : FrameLayout(context, attributeSet) {

    init {
        View.inflate(context, R.layout.merge_bart_map_view, this)
    }
}
