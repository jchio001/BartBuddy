package com.app.jonathan.willimissbart.bsa

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.util.Consumer
import com.app.jonathan.willimissbart.R
import com.app.jonathan.willimissbart.utils.view.ViewInflater
import com.app.jonathanchiou.willimissbart.api.models.bsa.ApiBsa
import java.text.SimpleDateFormat
import java.util.*

class BsaView(
    context: Context,
    attributeSet: AttributeSet
) : LinearLayout(context, attributeSet), Consumer<ApiBsa> {

    init {
        inflate(context, R.layout.merge_bsa_view, this)
    }

    private val expirationDate: TextView = findViewById(R.id.expiration_date)
    private val description: TextView = findViewById(R.id.description)

    override fun accept(t: ApiBsa) {
        expirationDate.text = DATE_FORMATTER.format(t.expires!!)
        description.text = t.description.cDataSection
    }

    companion object : ViewInflater(R.layout.bsa_view) {

        private val DATE_FORMATTER = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("America/Los_Angeles")
        }
    }
}
