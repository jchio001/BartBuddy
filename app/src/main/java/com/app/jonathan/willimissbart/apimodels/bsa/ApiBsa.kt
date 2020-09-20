package com.app.jonathan.willimissbart.apimodels.bsa

import com.app.jonathanchiou.willimissbart.db.models.Bsa
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
class ApiBsa(
    @Json(name = "id") val id: String?,
    @Json(name = "description") val description: ApiCdataClass,
    @Json(name = "posted") val posted: Date?,
    @Json(name = "expires") val expires: Date?
) {

    fun toDbModel() : Bsa {
        return Bsa(
            id = id!!,
            description = description.cDataSection,
            postedDate = posted!!,
            expirationDate = expires!!
        )
    }
}
