package dk.clausr.koncert.api

import dk.clausr.koncert.api.models.MessageDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject

class MessageApi @Inject constructor(
    private val client: SupabaseClient
) {
    private val table = client.postgrest["messages"]

    suspend fun retrieveMessages(groupId: String): List<MessageDto> =
        table
            .select(columns = Columns.raw("*, ...profiles(profile_id:id, username)")) {
                filter { this.eq("group_id", groupId) }
            }
            .decodeList()

    suspend fun createMessage(
        content: String,
        groupId: String,
        imageUrl: String?,
    ) {
        table.insert(buildJsonObject {
            put("content", content)
            put("group_id", groupId)
            put("image_url", imageUrl)
        }) {
            select()
        }
    }

    suspend fun deleteMessage(id: Int) {
        table.delete {
            filter {
                MessageDto::id eq id
            }
        }
    }
}