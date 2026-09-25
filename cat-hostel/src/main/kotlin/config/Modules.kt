package pro.artkart.config

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import pro.artkart.controller.configureRouting

fun Application.myModule() {
    DB.connect()
    transaction {
        SchemaUtils.create(CatsTable)
    }
    install(ContentNegotiation) {
        json()
    }
    configureRouting()
}
