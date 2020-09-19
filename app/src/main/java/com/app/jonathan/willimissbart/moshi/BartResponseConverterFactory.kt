package com.app.jonathan.willimissbart.moshi

import com.app.jonathan.willimissbart.api.BartResponseWrapper
import com.squareup.moshi.Types
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class BartResponseConverterFactory : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, Any>? {
        val delegate = retrofit.nextResponseBodyConverter<BartResponseWrapper<*>>(
            this,
            Types.newParameterizedType(BartResponseWrapper::class.java, type),
            annotations
        )
        return Converter<ResponseBody, Any> { responseBody ->
            delegate.convert(responseBody)?.root
        }
    }
}
