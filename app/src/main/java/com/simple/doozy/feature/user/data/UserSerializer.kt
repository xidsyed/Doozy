package com.simple.doozy.feature.user.data

import androidx.datastore.core.Serializer
import com.simple.doozy.feature.auth.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object UserSerializer : Serializer<User?> {
    override val defaultValue: User? = null

    override suspend fun readFrom(input: InputStream): User? {
        return try {
            val content = input.readBytes().decodeToString()
            if (content.isBlank()) return null
            Json.decodeFromString(
                deserializer = User.serializer(),
                string = content
            )
        } catch (e: SerializationException) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: User?, output: OutputStream) {
        withContext(Dispatchers.IO) {
            if (t == null) {
                output.write(ByteArray(0))
            } else {
                output.write(
                    Json.encodeToString(
                        serializer = User.serializer(),
                        value = t
                    ).encodeToByteArray()
                )
            }
        }
    }
}
