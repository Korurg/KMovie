package com.github.korurg.jackett.domain.native

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

/**
 * Jackett normally returns `ffprobe` as an array of streams, but some indexers emit a single
 * object instead. This replaces Jackson's ACCEPT_SINGLE_VALUE_AS_ARRAY for that field.
 */
object NativeProbeStreamListSerializer : KSerializer<List<NativeProbeStream>?> {
    private val delegate = ListSerializer(NativeProbeStream.serializer())

    override val descriptor: SerialDescriptor = delegate.descriptor

    override fun deserialize(decoder: Decoder): List<NativeProbeStream>? {
        val jsonDecoder = decoder as? JsonDecoder ?: return delegate.deserialize(decoder)
        return when (val element = jsonDecoder.decodeJsonElement()) {
            is JsonNull -> null
            is JsonArray -> jsonDecoder.json.decodeFromJsonElement(delegate, element)
            is JsonObject -> listOf(jsonDecoder.json.decodeFromJsonElement(NativeProbeStream.serializer(), element))
            else -> throw SerializationException("ffprobe must be an array or an object")
        }
    }

    @OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: List<NativeProbeStream>?) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: return if (value == null) encoder.encodeNull() else delegate.serialize(encoder, value)
        val element: JsonElement = value?.let { jsonEncoder.json.encodeToJsonElement(delegate, it) } ?: JsonNull
        jsonEncoder.encodeJsonElement(element)
    }
}
