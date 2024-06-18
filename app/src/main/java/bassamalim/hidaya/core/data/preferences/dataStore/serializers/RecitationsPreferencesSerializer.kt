package bassamalim.hidaya.core.data.preferences.dataStore.serializers

import androidx.datastore.core.Serializer
import bassamalim.hidaya.core.data.preferences.dataStore.objects.RecitationsPreferences
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object RecitationsPreferencesSerializer: Serializer<RecitationsPreferences> {

    override val defaultValue: RecitationsPreferences
        get() = RecitationsPreferences()

    override suspend fun readFrom(input: InputStream): RecitationsPreferences {
        return try {
            Json.decodeFromString(
                deserializer = RecitationsPreferences.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (e: SerializationException) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: RecitationsPreferences, output: OutputStream) {
        output.write(
            Json.encodeToString(
                serializer = RecitationsPreferences.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }

}