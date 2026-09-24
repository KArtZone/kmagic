package pro.artkart.config

import io.ktor.server.application.Application
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import pro.artkart.db.CatsTable

fun Application.configureDatabase() {
    DB.connect()
    transaction {
        SchemaUtils.create(CatsTable)
    }
}

object DB {
    fun connect() = connect(
        url = System.getenv("DB_URL"),
        driver = System.getenv("DB_DRIVER"),
        user = System.getenv("DB_USER"),
        password = System.getenv("DB_PASSWORD")
    )

    private fun connect(
        url: String,
        driver: String,
        user: String,
        password: String
    ) = Database.connect(
        url = url,
        driver = driver,
        user = user,
        password = password,
    )
}
