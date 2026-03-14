package com.simple.doozy.feature.subscription.data

import androidx.datastore.core.Serializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object SubscriptionSerializer : Serializer<SubscriptionData> {
    override val defaultValue: SubscriptionData
        get() = SubscriptionData.NoSubscription()

    override suspend fun readFrom(input: InputStream): SubscriptionData {
        return try {
            Json.decodeFromString(
                deserializer = SubscriptionData.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (e: SerializationException) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: SubscriptionData, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.write(
                Json.encodeToString(
                    serializer = SubscriptionData.serializer(),
                    value = t
                ).encodeToByteArray()
            )
        }
    }
}
