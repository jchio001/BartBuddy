package com.app.jonathanchiou.willimissbart.api.models

import com.squareup.moshi.*
import java.io.IOException
import java.lang.reflect.Type

@JsonQualifier
annotation class SingleToList

class SingleToListFactory : JsonAdapter.Factory {

    override fun create(type: Type, annotations: Set<Annotation>, moshi: Moshi): JsonAdapter<*>? {
        val delegateAnnotations = Types.nextAnnotations(annotations, SingleToList::class.java) ?: return null

        if (Types.getRawType(type) != List::class.java) {
            throw IllegalArgumentException(
                "Only lists may be annotated with @SingleToList. Found: $type")
        }

        val elementType = Types.collectionElementType(type, List::class.java)
        val delegateAdapter = moshi.adapter<List<Any>>(type, delegateAnnotations)
        val elementAdapter = moshi.adapter<Any>(elementType)

        return Adapter(delegateAdapter, elementAdapter)
    }
}

class Adapter internal constructor(internal val delegateAdapter: JsonAdapter<List<Any>>,
                                   internal val elementAdapter: JsonAdapter<Any>) :
    JsonAdapter<List<Any>>() {

    @Throws(IOException::class)
    override fun fromJson(reader: JsonReader): List<Any>? {
        return if (reader.peek() != JsonReader.Token.BEGIN_ARRAY)
            listOf(elementAdapter.fromJson(reader)!!)
        else
            delegateAdapter.fromJson(reader)
    }

    @Throws(IOException::class)
    override fun toJson(writer: JsonWriter, value: List<Any>?) {
        return if (value!!.size == 1)
            elementAdapter.toJson(writer, value[0])
        else
            delegateAdapter.toJson(writer, value)
    }
}
