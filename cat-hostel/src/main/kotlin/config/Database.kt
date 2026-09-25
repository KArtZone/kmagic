package pro.artkart.config

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.jdbc.Database


object DB {
    fun connect() = Database.connect(
        url = "jdbc:postgresql://localhost:5432/cat_hostel",
        driver = "org.postgresql.Driver",
        user = "admin",
        password = "abcd1234"
    )
}

object CatsTable : IntIdTable() {
    val name = varchar("name", 100).uniqueIndex()
    val age = integer("age")
}

@Serializable
data class CatDto(
    val id: Int? = null,
    val name: String,
    val age: Int
)
