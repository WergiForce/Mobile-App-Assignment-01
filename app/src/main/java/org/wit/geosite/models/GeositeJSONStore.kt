package org.wit.geosite.models

import android.content.Context
import android.net.Uri
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import org.wit.geosite.helpers.*
import timber.log.Timber
import java.lang.reflect.Type
import java.util.*

const val JSON_FILE = "geosites.json"
val gsonBuilder: Gson = GsonBuilder().setPrettyPrinting()
    .registerTypeAdapter(Uri::class.java, UriParser())
    .create()
val listType: Type = object : TypeToken<ArrayList<GeositeModel>>() {}.type

fun generateRandomId(): Long {
    return Random().nextLong()
}

class GeositeJSONStore(private val context: Context) : GeositeStore {

    var geosites = mutableListOf<GeositeModel>()

    init {
        if (exists(context, JSON_FILE)) {
            deserialize()
        }
    }

    override fun findAll(): MutableList<GeositeModel> {
        logAll()
        return geosites
    }

    override fun create(geosite: GeositeModel) {
        geosite.id = generateRandomId()
        geosites.add(geosite)
        serialize()
    }


    override fun update(geosite: GeositeModel) {
        // todo
    }

    private fun serialize() {
        val jsonString = gsonBuilder.toJson(geosites, listType)
        write(context, JSON_FILE, jsonString)
    }

    private fun deserialize() {
        val jsonString = read(context, JSON_FILE)
        geosites = gsonBuilder.fromJson(jsonString, listType)
    }

    private fun logAll() {
        geosites.forEach { Timber.i("$it") }
    }
}

class UriParser : JsonDeserializer<Uri>,JsonSerializer<Uri> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Uri {
        return Uri.parse(json?.asString)
    }

    override fun serialize(
        src: Uri?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src.toString())
    }
}