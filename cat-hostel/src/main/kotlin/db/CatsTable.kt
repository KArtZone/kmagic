package pro.artkart.db

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object CatsTable : IntIdTable() {
    val name = varchar("name", 100).uniqueIndex()
    val age = integer("age")
}

@Serializable
data class CatDto(
    val name: String,
    val age: Int
)
