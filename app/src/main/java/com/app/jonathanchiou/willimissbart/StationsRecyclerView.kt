package com.app.jonathanchiou.willimissbart

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import androidx.core.util.Consumer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class StationsRecyclerView(context: Context, attributeSet: AttributeSet):
    RecyclerView(context, attributeSet) {

    val stationsAdapter = StationsAdapter(this)

    lateinit var selectionType: String

    init {
        stationsAdapter.onClickListener = Consumer { station ->
            val intent = Intent()
            intent.putExtra(StationSelectionActivity.STATION_SELECTION_TYPE, selectionType)
            intent.putExtra(StationSelectionActivity.SELECTED_STATION_KEY, station)

            (context as Activity).also {
                it.setResult(Activity.RESULT_OK, intent)
                it.finish()
            }
        }

        layoutManager = LinearLayoutManager(context)
        adapter = stationsAdapter
    }
}