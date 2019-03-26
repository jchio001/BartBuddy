package com.app.jonathanchiou.willimissbart

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick

import com.app.jonathanchiou.willimissbart.StationSelectionActivity.Companion.STATION_SELECTION_TYPE
import com.app.jonathanchiou.willimissbart.StationSelectionActivity.Companion.ORIGIN_SELECTION
import com.app.jonathanchiou.willimissbart.StationSelectionActivity.Companion.DESTINATION_SELECTION
import com.app.jonathanchiou.willimissbart.StationSelectionActivity.Companion.SELECTED_STATION_KEY

class TripSelectionFragment: Fragment() {

    @BindView(R.id.origin_station_textview)
    lateinit var originStationTextView: TextView

    @BindView(R.id.destination_station_textview)
    lateinit var destinationStationTextView: TextView

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_trip_selection, container, false)
    }

    override fun onViewCreated(view: View,
                               savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == STATIONS_SELECTION_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val selectionType = data!!.getStringExtra(STATION_SELECTION_TYPE)
                val station = data.getParcelableExtra<Station>(SELECTED_STATION_KEY)

                (if (selectionType == ORIGIN_SELECTION) originStationTextView
                else destinationStationTextView).text = station.name
            }
        }
    }

    @OnClick(R.id.origin_station_textview, R.id.destination_station_textview)
    fun onStationTextViewClicked(view: View) {
        val intent = Intent(context, StationSelectionActivity::class.java)
        intent.putExtra(
            STATION_SELECTION_TYPE,
            if (view.id == R.id.origin_station_textview) ORIGIN_SELECTION
            else DESTINATION_SELECTION)

        startActivityForResult(intent, STATIONS_SELECTION_CODE)
    }

    companion object {
        const val STATIONS_SELECTION_CODE = 73
    }
}